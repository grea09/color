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
import me.grea.antoine.soda.type.Action;
import me.grea.antoine.soda.type.Edge;
import me.grea.antoine.soda.type.Flaw;
import me.grea.antoine.soda.type.Problem;
import me.grea.antoine.soda.type.Resolver;
import me.grea.antoine.soda.type.SubGoal;
import me.grea.antoine.soda.type.Threat;
import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.graph.DefaultDirectedGraph;

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

    public void solve() throws Success, Failure {
        Log.d(agenda);
        if(agenda.isEmpty())
            throw new Success();
        Flaw flaw = agenda.pop();
        Log.d("Resolving " + flaw);
        Deque<Resolver> resolvers = flaw.resolvers();
        Log.d("Resolver " + resolvers);
        for(Resolver resolver : resolvers)
        {
            Log.d("Trying with " + resolver);
            
            if(!resolver.duplicate(problem.plan)) {
                resolver.apply(problem.plan);
            }
            else
            {
                continue;
            }
            
            if(!new CycleDetector<>(problem.plan).detectCyclesContainingVertex(resolver.target))
            {
                Set<Flaw> related = resolver.related(problem);
                for(Flaw newFlaw : related)
                {
                    if(! agenda.contains(newFlaw))
                    {
                        agenda.addLast(newFlaw);
                    }
                }
                solve();
            }
            else
            {
                Log.w("REVERT !");
                resolver.revert(problem.plan);
            }
            problem.partialSolutions.put(flaw, (DirectedGraph<Action, Edge>) ((DefaultDirectedGraph<Action, Edge>) problem.plan).clone());
        }
        Log.w("Plan unsolvable !!!");
        throw new Failure(flaw);
    }
}
