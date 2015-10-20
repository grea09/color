/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.soda.algorithm;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import me.grea.antoine.soda.type.Action;
import me.grea.antoine.soda.type.Edge;
import me.grea.antoine.soda.type.Goal;

/**
 *
 * @author antoine
 */
public class ProperPlan {

    public static DirectedGraph<Action, Edge> plan(Goal goal, Set<Action> actions) {
        DirectedGraph<Action, Edge> plan = new DefaultDirectedGraph<>(Edge.class);
        Set<Action> relevants = satisfy(goal, actions, plan);
        Deque<Action> open = new ArrayDeque<>(relevants);
        while (!open.isEmpty()) {
            Action action = open.pop();
            Set<Action> candidates = satisfy(goal, actions, plan);
            for (Action candidate : candidates) {
                if (!relevants.contains(candidate)) {
                    open.push(candidate);
                }
            }
            relevants.addAll(candidates);

        }
        return plan; //TODO unthreaten + loop a little ?
    }
    

    private static Set<Action> satisfy(Goal goal, Set<Action> actions, DirectedGraph<Action, Edge> plan) {
        Set<Action> relevants = new HashSet<>();
        plan.addVertex((Action) goal);
        for (int precondition : goal.preconditions) {
            for (Action action : actions) {
                if (action.effects.contains(precondition)) {
                    plan.addVertex(action);
                    Edge edge = plan.addEdge(action, (Action) goal);
                    if (edge != null) // FIXME Find a way for that
                    {
                        edge.label = precondition;
                    }
                    relevants.add(action);
                }
            }
        }
        return relevants;
    }

}
