/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.lollipop.algorithm;

import java.util.Deque;
import java.util.Set;
import me.grea.antoine.lollipop.agenda.Agenda;
import me.grea.antoine.lollipop.agenda.ClassicalAgenda;
import me.grea.antoine.lollipop.exception.Success;
import me.grea.antoine.lollipop.type.Problem;
import me.grea.antoine.lollipop.type.flaw.Flaw;
import me.grea.antoine.lollipop.type.flaw.Resolver;
import me.grea.antoine.utils.Log;

/**
 *
 * @author antoine
 */
public class Lollipop {

    private final Problem problem;
    private Agenda agenda;

    public Lollipop(Problem problem) {
        this.problem = problem;
        agenda = new ClassicalAgenda(problem);
    }

    public static void solve(Problem problem) {
        Lollipop pop = new Lollipop(problem);
        while (true) {
            try {
                pop.refine();
            } catch (Success ex) {
                Log.i("Success !");
                return;
            }
            Log.w("Failure");
//            pop.agenda = new ClassicalAgenda(problem);
//            problem.plan = new Plan();
//            problem.plan.addVertex(problem.initial);
//            problem.plan.addVertex(problem.goal);
            
            return;
        }
    }

    public void refine() throws Success {
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
            Set<Flaw> related = resolver.related(problem);
            agenda.addAll(related);
            refine();
            resolver.revert(problem.plan); // Return means failure
            agenda.removeAll(related);
        }
        Log.w("No suitable resolver for " + flaw);
        Log.v(agenda);
        Log.v(problem.planToString());
    }

}
