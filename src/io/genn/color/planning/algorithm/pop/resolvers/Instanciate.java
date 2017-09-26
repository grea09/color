/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.genn.color.planning.algorithm.pop.resolvers;

import io.genn.color.planning.domain.Action;
import io.genn.color.planning.algorithm.Resolver;
import io.genn.color.planning.domain.State;
import io.genn.color.planning.domain.fluents.Fluent;
import io.genn.color.planning.plan.CausalLink;
import io.genn.color.planning.algorithm.Change;
import io.genn.color.planning.plan.Plan;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import static me.grea.antoine.utils.collection.Collections.list;
import me.grea.antoine.utils.log.Log;

/**
 *
 * @author antoine
 */
public class Instanciate<F extends Fluent<F, E>, E> implements Resolver<F> {

	private final Action<F, E> existing;
	private final Action<F, E> adding;
	private final Set<Change> changes;
	private final Map<E, E> unification;
	private final Set<CausalLink> tree;
	private final Set<CausalLink> newTree;
	private Plan plan;
	private boolean addingExisted;
	private boolean existingExisted;

	public Instanciate(Action<F, E> from,
			Action<F, E> to, Map<E, E> unification) {
		this.existing = from;
		this.adding = to;
		this.unification = unification;
		this.tree = new HashSet<>();
		this.newTree = new HashSet<>();
		this.changes = new HashSet<>();
	}

	private void computeTree(Plan plan) {
		if (plan.containsVertex(adding)) {
			return;
		}

		Stack<Action<F, E>> open = new Stack<>();

		List<E> newParameters = existing.parameterize(unification);
		if (newParameters == null || existing.parameters.equals(
				newParameters)) {
			return;
		}

		open.push(existing);
		Set<Action<F, E>> closed = new HashSet<>();
		while (!open.empty()) {
			Action<F, E> current = open.pop();
			closed.add(current);

			for (CausalLink link : plan.outgoingEdgesOf(current)) {
				if (!closed.contains(link.target())) {
					open.push(link.target());
				}
				if (!tree.contains(link)) {
//					State instanciate = link.causes.instanciate(unification);
//					if (!instanciate.equals(link.causes)) {
					tree.add(link);
//					}
				}
			}
		}

	}

	@Override
	public boolean appliable(Plan plan) {
		if (plan.containsVertex(adding)) {
			return true;
		}

		if (tree.isEmpty()) {
			computeTree(plan);
		}
		if (unification.isEmpty()) {
			return false;
		}

		for (CausalLink link : tree) {
			State causes = link.causes.instanciate(unification);
			if (!causes.coherent()) {
				return false;
			}
			Action<F, E> source = link.source().instanciate(unification);
			Action<F, E> target = link.target().instanciate(unification);
//			if (adding.equals(target) ||
//					plan.reachable(adding, link.source()) ||
//					plan.reachable(target, source)) {
//				return false;
//			}

			if (source == null || target == null || causes == null) {
				return false;
			}

			newTree.add(new CausalLink(source, target, causes));
		}

		return true;
	}

	@Override
	public void apply(Plan plan) {
		this.plan = plan;
		if (plan.containsVertex(adding)) {
			return;
		}

		if (unification.isEmpty()) {
			return;
		}

		addingExisted = plan.containsVertex(adding);
		existingExisted = plan.containsVertex(existing);

		for (CausalLink link : tree) {
			changes.add(new Change(plan, link,
								   existing.equals(link.source()),
								   existing.equals(link.target())));
			plan.removeEdge(link);
		}

		plan.removeVertex(existing);
		plan.addVertex(adding);

		for (CausalLink link : newTree) {
			changes.add(new Change(plan, link));
			plan.addEdge(link);
		}
	}

	@Override
	public void revert() {
//		Log.d("Reverting " + changes);

		for (Change reverter : changes) {
			reverter.undo();
		}
		
		if (!addingExisted) {
			plan.removeVertex(adding);
		} else {
			plan.addVertex(adding);
		}
		if (!existingExisted) {
			plan.removeVertex(existing);
		} else {
			plan.addVertex(existing);
		}
	}

	@Override
	public Collection<Change> changes() {
		return changes;
	}

	@Override
	public State<F> provides() {
		return adding.eff;
	}

	@Override
	public String toString() {
		return existing + " ± " + adding;
	}

}
