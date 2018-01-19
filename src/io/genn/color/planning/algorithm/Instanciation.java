/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.genn.color.planning.algorithm;

import io.genn.color.planning.domain.Action;
import io.genn.color.planning.domain.State;
import io.genn.color.planning.domain.fluents.Fluent;
import io.genn.color.planning.problem.CausalLink;
import io.genn.color.planning.problem.Plan;
import java.util.Deque;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import static me.grea.antoine.utils.collection.Collections.*;
import me.grea.antoine.utils.log.Log;

/**
 *
 * @author antoine
 */
public class Instanciation<F extends Fluent<F, E>, E> {

	public final Action<F, E> origin;
	public final Action<F, E> destination;
	public final Map<E, E> unification;
	public final Plan plan;
	public final Set<CausalLink> add;
	public final Set<CausalLink> del;

	public Instanciation(Action<F, E> origin, Map<E, E> unification, Plan plan) {
		this.origin = origin;
		this.destination = origin.instanciate(unification);
		this.unification = unification;
		this.plan = plan;
		add = new HashSet<>();
		del = new HashSet<>();
	}

	public boolean search() {
		Deque<Action<F, E>> open = queue(origin);
		Deque<Action<F, E>> closed = queue();
		Plan projection = new Plan(plan);
		while (!open.isEmpty()) {
			Action<F, E> current = open.pop();
			closed.push(current);
			if (plan.containsVertex(current)) {
				for (CausalLink link : plan.edgesOf(current)) {
					Action<F, E> source = link.source().instanciate(unification);
					Action<F, E> target = link.target().instanciate(unification);
					State<F> causes = link.causes.instanciate(unification);
					if (source == null || target == null || causes == null) {
						return false;
					}
					if (source.special()) { // Not instanciating init or goal !
						source = link.source();
					}
					if (target.special()) {
						target = link.target();
					}

					if (source != link.source() ||
							target != link.target() ||
							causes != link.causes) {
						del.add(link);
						CausalLink newLink =
								new CausalLink(source, target, causes);
						add.add(newLink);
						open.add(link.source());
						open.add(link.target());
						projection.removeEdge(link);
						try {
							projection.addEdge(newLink);
						} catch (IllegalStateException e)
						{
							Log.v(e);
							Log.v("Potential cycle detected, search is cancelled.");
							return false;
						}
						
					}
				}
			}
			open.removeAll(closed);
		}
		return true;
	}

	public Set<Change> apply() {
		Set<Change> changes = new HashSet<>();
		for (CausalLink link : del) {
			changes.add(new Change(plan, link));
			Log.v("Removing " + link);
			plan.removeEdge(link);
		}
		for (CausalLink link : add) {
			changes.add(new Change(plan, link));
			Log.v("Adding " + link);
			plan.addEdge(link);
		}
		for (Action action : new HashSet<>(plan.vertexSet())) { // Remove all useless actions
			if (!action.special() && plan.outDegreeOf(action) == 0) {
				Log.v("Removing useless action " + action);
				plan.removeVertex(action);
			}
		}
		return changes;

	}

}
