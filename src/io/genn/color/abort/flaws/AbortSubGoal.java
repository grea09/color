/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.genn.color.abort.flaws;

import io.genn.color.abort.problem.AbortSolution;
import io.genn.color.planning.algorithm.Resolver;
import io.genn.color.planning.domain.Action;
import io.genn.color.planning.domain.fluents.Fluent;
import io.genn.color.planning.problem.Problem;
import io.genn.color.pop.flaws.PopSubGoal;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 *
 * @author antoine
 */
public class AbortSubGoal<F extends Fluent<F, E>, E> extends PopSubGoal<F, E> {

	private int level;

	public AbortSubGoal(Problem problem) {
		super(problem);
		this.level = ((AbortSolution) problem.solution).level;
	}

	public AbortSubGoal(F fluent, Action<F, E> needer, Problem problem) {
		super(fluent, needer, problem);
		this.level = ((AbortSolution) problem.solution).level;
	}

	@Override
	public Deque<Resolver<F>> resolvers() {
		Deque<Resolver<F>> resolvers = new ArrayDeque<Resolver<F>>() {
			@Override
			public boolean add(Resolver e) {
				if (contains(e)) {
					return false;
				}
				return super.add(e);
			}
		};
		resolvers.addAll(solve(problem.initial));
		for (Action<F, E> step : problem.solution.working().vertexSet()) {
			if (!step.special()) {
				resolvers.addAll(solve(step));
			}
		}
		for (Action<F, E> action : problem.domain) {
			if (action.level >= level) {
				resolvers.addAll(solve(action));
			}
		}
		return resolvers;
	}

}
