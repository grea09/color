/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.genn.color.abort.resolvers;

import io.genn.color.abort.problem.AbstractSolution;
import io.genn.color.planning.algorithm.Change;
import io.genn.color.planning.algorithm.Resolver;
import io.genn.color.planning.domain.Action;
import io.genn.color.planning.domain.State;
import io.genn.color.planning.domain.fluents.Fluent;
import io.genn.color.planning.problem.CausalLink;
import io.genn.color.planning.problem.Plan;
import io.genn.color.planning.problem.Solution;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import me.grea.antoine.utils.log.Log;

/**
 *
 * @author antoine
 */
public class Expand<F extends Fluent<F, ?>> implements Resolver<F> {

	public final Action<F, ?> composite;
	public final boolean last;
	protected final Set<Change> changes;
	private AbstractSolution lastSolution;

	public Expand(
			Action<F, ?> composite, boolean last) {
		this.composite = composite;
		this.last = last;
		this.changes = new HashSet<>();
	}

	@Override
	public boolean appliable(Solution solution) {
		if (solution instanceof AbstractSolution && composite.method != null &&
				!composite.method.vertexSet().isEmpty()) {
			return last ? ((AbstractSolution) solution).level() > 0 : true;
		}
		return false;
	}

	@Override
	public void apply(Solution solution) {
		lastSolution = (AbstractSolution) solution;
		Plan current = lastSolution.working();
		Plan next = lastSolution.next();
		Plan method = composite.method;
		Action start = null, end = null;
		for (Action action : method.vertexSet()) {
			if (action.initial()) {
				start = action;
			}
			if (action.goal()) {
				end = action;
			}
		}
		if (start == null || end == null) {
			throw new IllegalStateException(
					"Methods must have an initial and goal actions !");
		}

		for (CausalLink link : method.edgeSet()) {
			changes.add(new Change(next, link));
			next.addEdge(link);
		}
		for (CausalLink in : next.incomingEdgesOf(composite)) {
			CausalLink newIn =
					new CausalLink(in.source(), start, in.causes);
			changes.add(new Change(next, in));
			changes.add(new Change(next, newIn));
			next.addEdge(newIn);
		}
		for (CausalLink out : next.outgoingEdgesOf(composite)) {
			CausalLink newOut =
					new CausalLink(end, out.target(), out.causes);
			changes.add(new Change(next, out));
			changes.add(new Change(next, newOut));
			next.addEdge(newOut);
		}
		next.removeVertex(composite);
		if (this.last) {
			Log.i("Next level !\n============================================");
			lastSolution.level(lastSolution.level() - 1);
		}
	}

	@Override
	public void revert() {
		if (this.last) {
			lastSolution.level(lastSolution.level() + 1);
		}
		for (Change change : changes) {
			change.undo();
		}
	}

	@Override
	public Collection<Change> changes() {
		return changes;
	}

	@Override
	public State<F> provides() {
		return composite.eff;
	}

	@Override
	public String toString() {
		return "+" + composite;
	}
	
	

}
