/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.soda.algorithm;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Set;
import me.grea.antoine.log.Log;
import me.grea.antoine.soda.exception.Failure;
import me.grea.antoine.soda.exception.Success;
import me.grea.antoine.soda.type.PartialSolution;
import me.grea.antoine.soda.type.flaw.Flaw;
import me.grea.antoine.soda.type.Problem;
import me.grea.antoine.soda.type.flaw.Resolver;
import me.grea.antoine.soda.type.flaw.SubGoal;
import me.grea.antoine.soda.type.flaw.Threat;
import org.jgrapht.alg.CycleDetector;

/**
 *
 * @author antoine
 */
public class PartialOrderPlanning {

    private final Problem problem;
    private final Deque<Flaw> agenda = new ArrayDeque<>();

    public PartialOrderPlanning(Problem problem) {
        this.problem = problem;
        populate(agenda, problem);
    }

    private static void populate(Deque<Flaw> agenda, Problem problem) {
        agenda.addAll(SubGoal.find(problem));
        agenda.addAll(Threat.find(problem));
    }

    public static void solve(Problem problem) throws Failure {
        PartialOrderPlanning pop = new PartialOrderPlanning(problem);
        try {
            pop.refine();
        } catch (Success ex) {
        }
        //Logically shouldn't go there
    }

    public void refine() throws Success, Failure {
//        Log.d("Agenda " + agenda);
        if (agenda.isEmpty()) {
            throw new Success();
        }
        Flaw flaw = agenda.pop();
//        Log.d("Resolving " + flaw);
        Deque<Resolver> resolvers = flaw.resolvers();
//        Log.d("Resolver " + resolvers);
        for (Resolver resolver : resolvers) {
//            Log.d("Trying with " + resolver);

            if (!resolver.duplicate(problem.plan)) {
                resolver.apply(problem.plan);
            } else {
                continue;
            }

            if (!new CycleDetector<>(problem.plan).detectCyclesContainingVertex(resolver.target)) {
                Set<Flaw> related = resolver.related(problem);
                for (Flaw newFlaw : related) {
                    if (!agenda.contains(newFlaw)) {
                        agenda.addLast(newFlaw);
                    }
                }
                refine();
            } else {
//                Log.w("REVERT !");
                resolver.revert(problem.plan);
            }
            problem.partialSolutions.push(new PartialSolution(problem.plan, flaw, agenda));
        }
        Log.w("Plan unsolvable !!!");
        problem.partialSolutions.push(new PartialSolution(problem.plan, flaw, agenda));
        throw new Failure(flaw);
    }
}
