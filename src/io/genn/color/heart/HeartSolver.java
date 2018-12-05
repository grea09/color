/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.genn.color.heart;

import io.genn.color.heart.flaws.Abstraction;
import io.genn.color.heart.resolvers.Expand;
import io.genn.color.planning.algorithm.Flaw;
import io.genn.color.planning.algorithm.Heuristic;
import io.genn.color.planning.algorithm.Resolver;
import io.genn.color.planning.problem.Problem;
import io.genn.color.pop.PopSolver;
import io.genn.color.pop.flaws.SubGoal;
import io.genn.color.pop.flaws.Threat;
import static me.grea.antoine.utils.collection.Collections.list;
import java.util.List;

/**
 *
 * @author antoine
 */
public class HeartSolver extends PopSolver {

	private final boolean last;
	
	protected HeartSolver(Problem problem, Heuristic heuristic, boolean last){
		super(problem, heuristic);
		this.last = last;
	}

	public HeartSolver(Flaw flaw, Problem problem, Heuristic heuristic, boolean last) {
		super(problem, heuristic);
		this.last = last;
		addAll(solve(flaw));
	}

	@Override
	protected <F extends Flaw> List<Resolver> solve(F flaw) {
		if (flaw instanceof SubGoal) {
			return super.solve((SubGoal) flaw);
		} else if (flaw instanceof Threat) {
			return super.solve((Threat) flaw);
		} else if (flaw instanceof Abstraction) {
			return solve((Abstraction) flaw);
		}
		throw new IllegalArgumentException("The flaw " + flaw + " of type " +
				flaw.getClass() + " is not handled by " + getClass());
	}

	protected List<Resolver> solve(Abstraction flaw) {
		List<Resolver> resolvers = list();
		resolvers.add(new Expand(flaw.needer, last, problem));
		return resolvers;
	}

}
