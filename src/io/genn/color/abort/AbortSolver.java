/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.genn.color.abort;

import io.genn.color.abort.flaws.Abstraction;
import io.genn.color.abort.heuristics.ProviderHeuristic;
import io.genn.color.abort.resolvers.Expand;
import io.genn.color.planning.algorithm.Flaw;
import io.genn.color.planning.algorithm.Resolver;
import io.genn.color.planning.problem.Problem;
import io.genn.color.pop.PopSolver;
import io.genn.color.pop.flaws.SubGoal;
import io.genn.color.pop.flaws.Threat;
import io.genn.color.pop.resolvers.Bind;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;

/**
 *
 * @author antoine
 */
public class AbortSolver extends PopSolver {

	private final boolean last;
	
	protected AbortSolver(Problem problem, boolean last){
		super(problem);
		this.last = last;
	}

	public AbortSolver(Flaw flaw, Problem problem, boolean last) {
		super(problem);
		this.last = last;
		addAll(solve(flaw));
	}

	@Override
	protected <F extends Flaw> Deque<Resolver> solve(F flaw) {
		if (flaw instanceof SubGoal) {
			List<Resolver> solve = new ArrayList<>(super.solve((SubGoal) flaw));
			Collections.sort(solve,new ProviderHeuristic());
			return new ArrayDeque<>(solve);
		} else if (flaw instanceof Threat) {
			return super.solve((Threat) flaw);
		} else if (flaw instanceof Abstraction) {
			return solve((Abstraction) flaw);
		}
		throw new IllegalArgumentException("The flaw " + flaw + " of type " +
				flaw.getClass() + " is not handled by " + getClass());
	}

	protected Deque<Resolver> solve(Abstraction flaw) {
		Deque<Resolver> resolvers = new ArrayDeque<>();
		resolvers.add(new Expand(flaw.needer, last));
		return resolvers;
	}

}
