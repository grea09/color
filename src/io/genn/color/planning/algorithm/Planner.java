/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.genn.color.planning.algorithm;

import io.genn.color.planning.problem.Problem;
import me.grea.antoine.utils.log.Log;

/**
 *
 * @author antoine
 */
public abstract class Planner {

	protected final Problem problem;
	protected final Heuristic heuristic;
	protected Agenda agenda;

	public Planner(Problem problem, Heuristic heuristic) {
		this.problem = problem;
		this.heuristic = heuristic;
	}

	public boolean solve() throws InterruptedException {
		Log.i(problem);
		if (problem.goal.pre.isEmpty()) {
			Log.i("Goal is empty !");
			return true;
		}

		try {
			Flaw fail = refine();
			problem.solution.end(false);
			Log.e("Failure : flaw " + fail + " is unsolvable !");
			return false;
		} catch (Success ex) {
			Log.i("Success !");
			problem.solution.end(true);
			return true;
		}
	}

	public abstract Flaw refine() throws Success, InterruptedException;

}
