/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.genn.color.planning.algorithm;

import io.genn.color.planning.problem.Problem;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author antoine
 */
public abstract class Solver extends ArrayList<Resolver> {

	public final Problem problem;
	public final Heuristic heuristic;
	
	protected Solver(Problem problem, Heuristic heuristic) {
		this.problem = problem;
		this.heuristic = heuristic;
	}
	
	public Solver(Flaw flaw, Problem problem, Heuristic heuristic) {
		this.problem = problem;
		this.heuristic = heuristic;
		addAll(solve(flaw));
	}
	
	protected abstract <F extends Flaw> List<Resolver> solve(F flaw);
}
