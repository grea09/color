/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.lollipop.type.flaw;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import me.grea.antoine.lollipop.heuristic.SimpleDegree;
import me.grea.antoine.lollipop.type.Action;
import me.grea.antoine.lollipop.type.Problem;

/**
 *
 * @author antoine
 */
public class SubGoal extends Flaw<SubGoal> {

    public SubGoal(int fluent, Action needer, Problem problem) {
        super(fluent, needer, problem);
    }

    private SubGoal(Problem problem) {
        super(problem);
    }
    
    @Override
    public Deque<Resolver> resolvers() {
        Deque<Resolver> resolvers = new ArrayDeque<>();
        if (problem.initial.effects.contains(fluent)) {
            resolvers.addLast(new Resolver(problem.initial, needer, fluent));
        }
        List<Action> steps = new ArrayList<>(problem.plan.vertexSet());
        steps.remove(problem.initial);
        steps.remove(problem.goal);
        steps.remove(needer);
        try {
            Collections.sort(steps, problem.heuristic.comparator()); // Order the domain by utility
        } catch (IllegalArgumentException ex) { // I DON'T CARE !!!!
        }
        for (Action step : steps) {
            if (step.effects.contains(fluent)) {
                resolvers.addLast(new Resolver(step, needer, fluent));
            }
        }
        Set<Action> actions = new HashSet<>(problem.domain);
        actions.remove(problem.initial);
        actions.remove(problem.goal);
        actions.remove(needer);
        actions.removeAll(steps);
        for (Action action : actions) {
            if (action.effects.contains(fluent)) {
                resolvers.addLast(new Resolver(action, needer, fluent));
            }
        }
        return resolvers;
    }
    
    public static Set<SubGoal> related(Action troubleMaker, Problem problem) {
        return new SubGoal(problem).related(troubleMaker);
    }

    @Override
    public Set<SubGoal> related(Action troubleMaker) {
        Set<SubGoal> related = new HashSet<>();
        Set<Integer> provided = new HashSet<Integer>() {
            {
                problem.plan.incomingEdgesOf(troubleMaker).stream().forEach((edge) -> {
                    addAll(edge.labels);
                });
            }
        };
        troubleMaker.preconditions.stream().filter((precondition) -> (!provided.contains(precondition))).forEach((precondition) -> {
            related.add(new SubGoal(precondition, troubleMaker, problem));
        });
        return related;
    }

    @Override
    public String toString() {
        return "? =(" + fluent + ")> " + needer;
    }

}
