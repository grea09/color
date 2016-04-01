/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.lollipop.type.flaw;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
        List<Action> providers = problem.providing.get(fluent);
        if (providers == null) {
            return resolvers;
        }
        for (Action action : providers) {
            if (action != needer) {
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
