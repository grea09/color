/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.genn.color.heart;

import io.genn.color.heart.problem.LeveledSolution;
import io.genn.color.planning.algorithm.Agenda;
import io.genn.color.planning.algorithm.Flaw;
import io.genn.color.planning.algorithm.Heuristic;
import io.genn.color.planning.algorithm.Resolver;
import io.genn.color.planning.algorithm.Solver;
import io.genn.color.planning.domain.Action;
import io.genn.color.planning.problem.Problem;
import io.genn.color.pop.Pop;

/**
 *
 * @author antoine
 */
public class Heart extends Pop {

	public Heart(Problem problem) {
		super(problem);
	}
	
	public Heart(Problem problem, Heuristic heuristic) {
		super(problem, heuristic);
	}

	@Override
	protected void init() {
		int level = 0;
		for (Action action : problem.domain) {
			if (level < action.level) {
				level = action.level;
			}
		}
		problem.solution = new LeveledSolution(level);
		problem.solution.working().addEdge(problem.initial, problem.goal);
		agenda = new HeartAgenda(problem);
	}

	@Override
	protected Solver solve(Flaw flaw) {
		return new HeartSolver(flaw, problem, heuristic, agenda.isEmpty());
	}

	@Override
	protected Agenda update(Resolver resolver) {
		Agenda oldAgenda = new HeartAgenda(agenda);
		agenda.update(resolver);
		return oldAgenda;
	}

}
