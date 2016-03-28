/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.lollipop.algorithm;

import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import me.grea.antoine.lollipop.type.Action;
import me.grea.antoine.lollipop.type.Domain;
import me.grea.antoine.lollipop.type.Edge;
import me.grea.antoine.lollipop.type.Plan;
import me.grea.antoine.lollipop.type.Problem;
import static me.grea.antoine.utils.Collections.set;
import static me.grea.antoine.utils.Collections.queue;

/**
 *
 * @author antoine
 */
public class DomainProperPlan {
    
    public static void inject(Problem problem)
    {
        add(set(problem.initial, problem.goal), problem.plan, problem.providing);
        thunderPrunning(problem.initial, problem.goal, problem.plan);
    }

    public static void add(Collection<Action> actions, Plan properPlan, Map<Integer, Set<Action>> providing) {
        for (Action target : actions) {
            properPlan.addVertex(target);
        }
        for (Action target : properPlan.vertexSet()) {
            for (int precondition : target.preconditions) {
                if (providing.containsKey(precondition)) {
                    for (Action source : providing.get(precondition)) {
                        properPlan.addVertex(source);
                        Edge edge = properPlan.getEdge(source, target);
                        if (edge == null) {
                            edge = properPlan.addEdge(source, target);
                        }
                        edge.labels.add(precondition);

                    }
                }
            }
        }
    }

    public static Plan build(Domain domain) {
        Plan operatorGraph = new Plan();
        add(domain, operatorGraph, domain.providing);
        return operatorGraph;
    }

    public static Map<Integer, Set<Action>> add(Collection<Action> actions, Map<Integer, Set<Action>> providing) {
        Map<Integer, Set<Action>> result = new HashMap<>(providing);

        for (Action action : actions) {
            for (int effect : action.effects) {
                if (result.containsKey(effect)) {
                    result.get(effect).add(action);
                } else {
                    result.put(effect, set(action));
                }
            }
        }
        return result;
        
    }

    public static Map<Integer, Set<Action>> providing(Domain domain) {
        Map<Integer, Set<Action>> providing = add(domain, new HashMap<>());
        return providing;
    }

    private static void thunderPrunning(Action initial, Action goal, Plan plan) {
        Set<Action> used = set(initial, goal);
        Deque<Action> forward = queue(initial);
        Deque<Action> backward = queue(goal);
        while(!forward.isEmpty() && !backward.isEmpty())
        {
            Set<Edge> forwardEdges = plan.outgoingEdgesOf(forward.pop());
            Set<Edge> backwardEdges = plan.incomingEdgesOf(backward.pop());
            
        }
    }

}
