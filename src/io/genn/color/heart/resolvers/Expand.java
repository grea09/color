/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.genn.color.heart.resolvers;

import io.genn.color.Benchmark;
import io.genn.color.heart.problem.LeveledSolution;
import io.genn.color.hipop.problem.CompositeSolution;
import io.genn.color.planning.algorithm.Change;
import io.genn.color.planning.algorithm.Resolver;
import io.genn.color.planning.algorithm.Success;
import io.genn.color.planning.domain.Action;
import io.genn.color.planning.domain.State;
import io.genn.color.planning.domain.fluents.Fluent;
import io.genn.color.planning.problem.CausalLink;
import io.genn.color.planning.problem.Plan;
import io.genn.color.planning.problem.Problem;
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
	private LeveledSolution lastSolution;
	private Problem problem; //FIXME remove after Benchmarks

	public Expand(
			Action<F, ?> composite, boolean last, Problem problem) {
		this.composite = composite;
		this.last = last;
		this.changes = new HashSet<>();
		this.problem = problem;
	}

	@Override
	public boolean appliable(Solution solution) {

		Plan next;
		Plan current = solution.working();
		if (solution instanceof LeveledSolution) {
			lastSolution = (LeveledSolution) solution;
			next = lastSolution.next();
		} else {
			next = current;
		}
		Plan test = new Plan(next);

		Action start = null, end = null;
		for (Action action : composite.method.vertexSet()) {
			if (action.initial()) {
				start = action;
			}
			if (action.goal()) {
				end = action;
			}
		}
		if (start == null || end == null) {
			return false;
		}

		try {
			for (CausalLink link : composite.method.edgeSet()) {
				test.addEdge(link);
			}
			for (CausalLink in : test.incomingEdgesOf(composite)) {
				CausalLink newIn =
						new CausalLink(in.source(), start, in.causes);
				test.addEdge(newIn);
			}
			for (CausalLink out : test.outgoingEdgesOf(composite)) {
				CausalLink newOut =
						new CausalLink(end, out.target(), out.causes);
				test.addEdge(newOut);
			}
		} catch (IllegalStateException e) {
			Log.v(e);
			Log.w("Cycle predicted, expansion cancelled !");
			return false;
		}

		if (composite.method != null && !composite.method.vertexSet().isEmpty()) {
			return last ? solution.level() > 0 : true;
		}
		return false;
	}

	@Override
	public void apply(Solution solution) {

		Plan next;
		Plan current = solution.working();
		if (solution instanceof LeveledSolution) {
			lastSolution = (LeveledSolution) solution;
			next = lastSolution.next();
		} else {
			next = current;
		}
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
			if (lastSolution != null) {
				Log.
						i("Next level !\n============================================");
				lastSolution.level(lastSolution.level() - 1);
				Benchmark.stops.put(problem, System.nanoTime());//FIXME remove after Benchmarks
			} else if (solution instanceof CompositeSolution) {
				CompositeSolution compositeSolution =
						(CompositeSolution) solution;
				compositeSolution.level(Integer.max(0,
													compositeSolution.level() -
													1));
			}
		}
	}

	@Override
	public void revert() {
		if (this.last && lastSolution != null) {
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
