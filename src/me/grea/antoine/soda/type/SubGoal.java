/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.soda.type;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import me.grea.antoine.log.Log;
import static me.grea.antoine.soda.utils.Collections.*;
import org.jgrapht.DirectedGraph;

/**
 *
 * @author antoine
 */
public class SubGoal extends Flaw {

    public SubGoal(int fluent, Action needer, Problem problem) {
        super(fluent, needer, problem);
    }

    @Override
    public Deque<Resolver> resolvers() {
        Deque<Resolver> resolvers = new ArrayDeque<>();
        if (problem.initial.effects.contains(fluent)) {
            resolvers.addLast(new Resolver(problem.initial, needer, fluent));
        }
        Set<Action> steps = new HashSet<>(problem.plan.vertexSet());
        steps.remove(problem.initial);
        steps.remove(needer);
        for (Action step : steps) {
            if (step.effects.contains(fluent)) {
                resolvers.addLast(new Resolver(step, needer, fluent));
            }
        }
        Set<Action> actions = new HashSet<>(problem.actions);
        actions.remove(problem.initial);
        actions.remove(needer);
        actions.removeAll(steps);
        for (Action action : actions) {
            if (action.effects.contains(fluent)) {
                resolvers.addLast(new Resolver(action, needer, fluent));
            }
        }
        return resolvers;
    }
    
    public static Set<SubGoal> find(Problem problem) {
        Map<Integer, SubGoal> subgoals = new HashMap<>();
        Deque<Action> open = queue(problem.goal);
        Set<Action> closed = set();
        while (!open.isEmpty()) {
            Action current = open.pollLast();
            closed.add(current);
//            for (int effect : current.effects) {
//                subgoals.remove(effect);
//            }
            for (int precondition : current.preconditions) {
//                if (!problem.initial.effects.contains(precondition)) {
                subgoals.put(precondition, new SubGoal(precondition, current, problem));
//                }
            }
            for (Edge edge : problem.plan.incomingEdgesOf(current)) {
                for(int label : edge.labels)
                {
                    subgoals.remove(label);
                }
                open.addFirst(problem.plan.getEdgeSource(edge));
            }
            open.removeAll(closed);
        }
        return new HashSet<>(subgoals.values());
    }
    
    public static int count(Plan plan, Action goal) {
        Set<Integer> subgoals = set();
        Deque<Action> open = queue(goal);
        Set<Action> closed = set();
        while (!open.isEmpty()) {
            Action current = open.pollLast();
            closed.add(current);
            for (int precondition : current.preconditions) {
                subgoals.add(precondition);
            }
            for (Edge edge : plan.incomingEdgesOf(current)) {
                subgoals.removeAll(edge.labels);
                open.addFirst(plan.getEdgeSource(edge));
            }
            open.removeAll(closed);
        }
        return subgoals.size();
    }

    public static Set<SubGoal> related(Action troubleMaker, Problem problem) {
        Set<SubGoal> related = new HashSet<>();
        for(int precondition : troubleMaker.preconditions)
        {
            if(troubleMaker != problem.initial && problem.plan.inDegreeOf(troubleMaker) == 0)
            related.add(new SubGoal(precondition, troubleMaker, problem));
        }
        return related;
    }
    
    @Override
        public String toString() {
            return "? =(" + fluent + ")> " + needer;
        }

}
