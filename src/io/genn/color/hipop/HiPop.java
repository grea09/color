/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.genn.color.hipop;

import io.genn.color.abort.Abort;
import io.genn.color.abort.AbortAgenda;
import io.genn.color.abort.AbortSolver;
import io.genn.color.abort.problem.LeveledSolution;
import io.genn.color.hipop.problem.CompositeSolution;
import io.genn.color.planning.algorithm.Agenda;
import io.genn.color.planning.algorithm.Flaw;
import io.genn.color.planning.algorithm.Resolver;
import io.genn.color.planning.algorithm.Solver;
import io.genn.color.planning.domain.Action;
import io.genn.color.planning.problem.Problem;
import io.genn.color.pop.problem.SimpleSolution;

/**
 *
 * @author antoine
 */
public class HiPop extends Abort {
	
	public HiPop(Problem problem) {
		super(problem);
	}
	
	@Override
	protected void init() {
		int level = 0;
		for (Action action : problem.domain) {
			if (level < action.level) {
				level = action.level;
			}
		}
		problem.solution = new CompositeSolution(level);
		problem.solution.working().addEdge(problem.initial, problem.goal);
		agenda = new HiPopAgenda(problem);
	}

	@Override
	protected Solver solve(Flaw flaw) {
		return new AbortSolver(flaw, problem,agenda.isEmpty());
	}

	@Override
	protected Agenda update(Resolver resolver) {
		Agenda oldAgenda = new HiPopAgenda(agenda);
		agenda.update(resolver);
		return oldAgenda;
	}
	
}
