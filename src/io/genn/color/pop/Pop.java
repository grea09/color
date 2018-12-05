/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.genn.color.pop;

import io.genn.color.pop.heuristics.SimpleHeuristic;
import io.genn.color.planning.algorithm.Success;
import io.genn.color.planning.algorithm.Planner;
import io.genn.color.planning.algorithm.Agenda;
import io.genn.color.planning.algorithm.Flaw;
import io.genn.color.planning.algorithm.Heuristic;
import io.genn.color.planning.algorithm.Resolver;
import io.genn.color.planning.algorithm.Solver;
import io.genn.color.planning.problem.Problem;
import io.genn.color.pop.problem.SimpleSolution;
import java.util.Collections;
import me.grea.antoine.utils.log.Log;

/**
 *
 * @author antoine
 */
public class Pop extends Planner {

	public Pop(Problem problem) {
		this(problem, new SimpleHeuristic());
	}
	
	public Pop(Problem problem, Heuristic heuristic) {
		super(problem, heuristic);
		init();
	}

	protected void init() {
		problem.solution = new SimpleSolution();
		problem.solution.working().addEdge(problem.initial, problem.goal);
		agenda = new PopAgenda(problem);
	}

	@Override
	public Flaw refine() throws Success, InterruptedException {
		if (Thread.interrupted()) {
			Log.e("Thread is interupted, aborting !");
			throw new InterruptedException("Time to stop !");
		}
		if (agenda.isEmpty()) {
			throw new Success();
		}
		Log.d("Agenda " + agenda);

		Flaw flaw = agenda.choose();
		Log.i("Resolving " + flaw);

		Solver resolvers = solve(flaw);
		Log.d("Resolvers " + resolvers);

		for (Resolver resolver : resolvers) {
			Log.i("Trying with " + resolver);
			if (resolver.appliable(problem.solution)) {
				resolver.apply(problem.solution);
			} else {
				Log.w(resolver + " isn't appliable !");
				continue;
			}
			Agenda oldAgenda = update(resolver);
			refine(); // Return means failure
			Log.i("Reverting the application of " + resolver);
			resolver.revert();
			Log.d("Restoring agenda to " + oldAgenda);
			agenda = oldAgenda;
		}
		agenda.add(flaw);
		Log.w("No suitable resolver for " + flaw);
		return flaw;
	}

	protected Solver solve(Flaw flaw) {
		Solver resolvers = new PopSolver(flaw, problem, heuristic);
		return resolvers;
	}

	protected Agenda update(Resolver resolver) {
		Agenda oldAgenda = new PopAgenda(agenda);
		agenda.update(resolver);
		return oldAgenda;
	}

}
