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
import me.grea.antoine.soda.type.Action;
import me.grea.antoine.soda.type.Edge;
import me.grea.antoine.soda.type.Goal;
import me.grea.antoine.soda.type.Plan;
import static me.grea.antoine.soda.utils.Collections.*;

/**
 *
 * @author antoine
 */
public class ProperPlan {

    public static Plan properPlan(Goal goal, Set<Action> actions) {
        Plan plan = new Plan();
        Set<Action> relevants = satisfy(goal, actions, plan);
        /* The satisfy function iterate throught 
        participating actions and add the causal links */
        Deque<Action> open = new ArrayDeque<>(relevants);
        //queue of opened relevant actions
        while (!open.isEmpty()) {
            Action action = open.pop();
            Set<Action> candidates = satisfy(action, actions, plan);
            /* Searching for candidates that satisfy 
            the selected providing action*/
            for (Action candidate : candidates) {
                if (!relevants.contains(candidate)) {
                    /* Only the one not threated already 
                    are added to ensure convergence */
                    open.push(candidate);
                }
            }
            relevants.addAll(candidates);
        }
        return plan;
    }

    private static Set<Action> satisfy(Goal goal, Set<Action> actions, Plan plan) {
        Set<Action> relevants = new HashSet<>();
        plan.addVertex((Action) goal);
        for (int precondition : goal.preconditions) {
            for (Action action : actions) {
                if (action.effects.contains(precondition)) {
                    plan.addVertex(action);
                    Edge edge = plan.addEdge(action, (Action) goal);
                    if (edge != null) {
                        edge.labels = set(precondition);
                    } else {
                        plan.getEdge(action, (Action) goal).labels.add(precondition);
                    }
                    relevants.add(action);
                }
            }
        }
        return relevants;
    }

}
