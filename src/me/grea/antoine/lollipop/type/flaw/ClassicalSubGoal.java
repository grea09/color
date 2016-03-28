/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.lollipop.type.flaw;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;
import me.grea.antoine.lollipop.type.Action;
import me.grea.antoine.lollipop.type.Problem;
import static me.grea.antoine.utils.Collections.union;
import static me.grea.antoine.utils.Collections.union;

/**
 *
 * @author antoine
 */
public class ClassicalSubGoal extends Flaw<ClassicalSubGoal> {

    public ClassicalSubGoal(int fluent, Action needer, Problem problem) {
        super(fluent, needer, problem);
    }

    private ClassicalSubGoal(Problem problem) {
        super(problem);
    }

    @Override
    public Deque<Resolver> resolvers() {
        Deque<Resolver> resolvers = new ArrayDeque<>();
        Set<Action> actions = union(problem.plan.vertexSet(), problem.domain);
        for (Action action : actions) {
            if (action.effects.contains(fluent)) {
                resolvers.addLast(new ClassicalResolver(action, needer, fluent));
            }
        }
        return resolvers;
    }

    public static Set<ClassicalSubGoal> related(Action troubleMaker, Problem problem) {
        return new ClassicalSubGoal(problem).related(troubleMaker);
    }

    @Override
    public Set<ClassicalSubGoal> related(Action troubleMaker) {
        Set<ClassicalSubGoal> related = new HashSet<>();
        Set<Integer> provided = new HashSet<Integer>() {
            {
                problem.plan.incomingEdgesOf(troubleMaker).stream().forEach((edge) -> {
                    addAll(edge.labels);
                });
            }
        };
        troubleMaker.preconditions.stream().filter((precondition) -> (!provided.contains(precondition))).forEach((precondition) -> {
            related.add(new ClassicalSubGoal(precondition, troubleMaker, problem));
        });
        return related;
    }

    @Override
    public String toString() {
        return "? =(" + fluent + ")> " + needer;
    }

}
