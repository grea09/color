/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.soda.type;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;
import me.grea.antoine.log.Log;
import me.grea.antoine.soda.exception.Failure;
import me.grea.antoine.soda.utils.DFS;
import static me.grea.antoine.soda.utils.Collections.*;
import org.jgrapht.DirectedGraph;

/**
 *
 * @author antoine
 */
public class Threat extends Flaw {

    public Action breaker;
    public Edge threatened;
    private Edge demoter;
    private Edge promoter;

    public Threat(Action threat, Edge threatened, int fluent, Problem problem) {
        super(fluent, problem.plan.getEdgeTarget(threatened), problem);
        this.breaker = threat;
        this.threatened = threatened;
    }

    @Override
    public Deque<Resolver> resolvers() {
        Deque<Resolver> resolvers = new ArrayDeque<>();
        if (problem.plan.getEdgeTarget(threatened) != problem.goal) {
//                Log.w("Can't demote after goal step !");
            resolvers.add(new Resolver(problem.plan.getEdgeTarget(threatened), breaker, 0));
        }
        if (problem.plan.getEdgeSource(threatened) != problem.initial) {
//                Log.w("Can't promote before initial step !");
            resolvers.add(new Resolver(breaker, problem.plan.getEdgeSource(threatened), 0));
        }
        return resolvers;
    }

    public static Set<Threat> find(Problem problem) {
        Set<Threat> threats = new HashSet<>();
        for (Action candidate : problem.plan.vertexSet()) {
            threats.addAll(related(candidate, problem));
        }
        return threats;
    }

    @Override
    public Set<Resolver> healers() {
        Set<Resolver> healers = super.healers();

        return union(healers, set(new Resolver(breaker, healers.iterator().next().source, 0)));
    }

    public static int count(DirectedGraph<Action, Edge> plan) {
        int count = 0;
        for (Action troubleMaker : plan.vertexSet()) {
            for (int effect : troubleMaker.effects) {
                for (Edge oposite : plan.edgeSet()) {
                    for (int fluent : oposite.labels) {
                        if (fluent == -effect) { //NEVER have a 0 effect
                            Action source = plan.getEdgeSource(oposite);
                            Action target = plan.getEdgeTarget(oposite);
                            if (!DFS.reachable(plan, troubleMaker, source) && !DFS.reachable(plan, target, troubleMaker)) {
                                count++;
                            }
                        }
                    }
                }
            }
        }
        return count;
    }

    public static Set<Threat> related(Action troubleMaker, Problem problem) {
        Set<Threat> related = new HashSet<>();
        for (int effect : troubleMaker.effects) {
            for (Edge oposite : problem.plan.edgeSet()) {
                for (int fluent : oposite.labels) {
                    if (fluent == -effect) { //NEVER have a 0 effect
                        Action source = problem.plan.getEdgeSource(oposite);
                        Action target = problem.plan.getEdgeTarget(oposite);
                        if (!DFS.reachable(problem.plan, troubleMaker, source) && !DFS.reachable(problem.plan, target, troubleMaker)) {
                            related.add(new Threat(troubleMaker, oposite, fluent, problem));
                        }
                    }
                }
            }
        }
        return related;
    }

    @Override
    public String toString() {
        return breaker + " ☠ " + threatened;
    }

}
