/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.genn.color.planning.algorithm.pop;

import java.util.Deque;
import io.genn.color.planning.algorithm.Planner;
import io.genn.color.planning.algorithm.Success;
import io.genn.color.planning.flaw.Agenda;
import io.genn.color.planning.flaw.Flaw;
import io.genn.color.planning.flaw.Resolver;
import io.genn.color.planning.problem.Problem;
import me.grea.antoine.utils.log.Log;

/**
 *
 * @author antoine
 */
public class Pop extends Planner{

    public Pop(Problem problem) {
        super(problem);
        agenda = new PopAgenda(problem);
    }

    @Override
    public Flaw refine() throws Success {
        if (agenda.isEmpty()) {
            throw new Success();
        }
        Log.d("Agenda " + agenda);

        Flaw flaw = agenda.choose();
        Log.d("Resolving " + flaw);

        Deque<Resolver> resolvers = flaw.resolvers();
        Log.d("Resolvers " + resolvers);

        for (Resolver resolver : resolvers) {
            Log.d("Trying with " + resolver);

            if (resolver.appliable(problem.plan)) {
                resolver.apply(problem.plan);
            } else {
                Log.w(resolver + " isn't appliable !");
                continue;
            }
            Agenda oldAgenda = new PopAgenda(agenda);
            agenda.related(resolver);
            refine();
            Log.w("Failure reverting the application of " + resolver);
            resolver.revert(); // Return means failure
            Log.w("Restoring agenda to " + oldAgenda);
            agenda = oldAgenda;
        }
        agenda.add(flaw);
        Log.w("No suitable resolver for " + flaw);
//        Log.d(agenda);
//        Log.v(problem.planToString());
        return flaw;
    }
    
}
