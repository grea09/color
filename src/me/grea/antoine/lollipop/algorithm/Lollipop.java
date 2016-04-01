/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.lollipop.algorithm;

import me.grea.antoine.lollipop.mechanism.ProperPlan;
import me.grea.antoine.lollipop.mechanism.Ranking;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Set;
import me.grea.antoine.lollipop.agenda.Agenda;
import me.grea.antoine.lollipop.agenda.LollipopAgenda;
import me.grea.antoine.lollipop.exception.Success;
import me.grea.antoine.lollipop.type.Action;
import me.grea.antoine.lollipop.type.Problem;
import me.grea.antoine.lollipop.type.flaw.Flaw;
import me.grea.antoine.lollipop.type.flaw.Resolver;
import static me.grea.antoine.utils.Collections.set;
import me.grea.antoine.utils.Log;

/**
 *
 * @author antoine
 */
public class Lollipop {

    private Agenda agenda;
    private Problem problem;

    public Lollipop(Problem problem) {
        this.problem = problem;
        ProperPlan properPlan = new ProperPlan(problem.domain.properPlan);
        properPlan.cache(set(problem.initial, problem.goal));
        
        for (Map.Entry<Action, Action> entry : problem.plan.updated.entrySet()) {
            properPlan.update(entry.getKey(), entry.getValue());
        }
        
        problem.providing = properPlan.providing;
        
        problem.ranking = new Ranking(problem.domain.ranking);
        problem.ranking.realize(problem);
        
        
        for (List<Action> value : problem.providing.values()) {
            Collections.sort(value, problem.ranking);
        }
        
        if (problem.plan.edgeSet().isEmpty()) {
            ProperPlan.sanic(problem);
        }
//        IllegalFixer.clean(problem);
        agenda = new LollipopAgenda(problem);
    }

    public static void solve(Problem problem) {
        Lollipop lollipop = new Lollipop(problem);
        while (true) {
            try {
                lollipop.refine();
            } catch (Success ex) {
                Log.i("Success !");
                problem.plan.updated.clear();
                return;
            }
            Log.w("Failure");
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
            Set<Flaw> invalidated = resolver.invalidated(agenda, problem);
            agenda.addAll(related);
            agenda.removeAll(invalidated);
            refine();
            resolver.revert(problem.plan); // Return means failure
            agenda.removeAll(related);
            agenda.addAll(invalidated);
        }
        agenda.add(flaw);
        Log.w("No suitable resolver for " + flaw);
        Log.v(agenda);
        Log.v(problem.planToString());
    }

}
