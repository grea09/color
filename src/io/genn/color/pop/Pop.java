/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.genn.color.pop;

import io.genn.color.planning.algorithm.Success;
import java.util.Deque;
import io.genn.color.planning.algorithm.Planner;
import io.genn.color.planning.algorithm.Agenda;
import io.genn.color.planning.algorithm.Flaw;
import io.genn.color.planning.algorithm.Resolver;
import io.genn.color.planning.problem.Problem;
import io.genn.color.pop.problem.PopSolution;
import me.grea.antoine.utils.log.Log;

/**
 *
 * @author antoine
 */
public class Pop extends Planner {

	public Pop(Problem problem) {
		super(problem);
		problem.solution = new PopSolution();
		problem.solution.working().addEdge(problem.initial, problem.goal);
		agenda = new PopAgenda(problem);
	}

	@Override
	public Flaw refine() throws Success {
		if (agenda.isEmpty()) {
			throw new Success();
		}
		Log.d("Agenda " + agenda);

		Flaw flaw = agenda.choose();
		Log.i("Resolving " + flaw);

		Deque<Resolver> resolvers = flaw.resolvers();
		Log.d("Resolvers " + resolvers);

		for (Resolver resolver : resolvers) {
			Log.i("Trying with " + resolver);
			if (resolver.appliable(problem.solution.working())) {
				resolver.apply(problem.solution.working());
			} else {
				Log.w(resolver + " isn't appliable !");
				continue;
			}
			Agenda oldAgenda = new PopAgenda(agenda);
			agenda.related(resolver);
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

}
