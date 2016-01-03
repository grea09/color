/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.soda.type.flaw;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;
import me.grea.antoine.soda.type.Action;
import me.grea.antoine.soda.type.Edge;
import me.grea.antoine.soda.type.Plan;
import me.grea.antoine.soda.type.Problem;
import me.grea.antoine.utils.Collections;
import static me.grea.antoine.utils.Collections.set;
import static me.grea.antoine.utils.Collections.queue;

/**
 *
 * @author antoine
 */
public class Orphan extends Flaw {

    public Orphan(Action orphan, Problem problem) {
        super(0, orphan, problem);
    }

    @Override
    public Deque<Resolver> resolvers() {
        return queue(new Resolver(needer, null, 0, true));
    }

    public static Set<Orphan> find(Problem problem) {
        Set<Orphan> orphans = new HashSet<>();
        problem.plan.vertexSet().stream().filter((candidate)
                -> (problem.plan.outDegreeOf(candidate) == 0
                && !problem.goal.equals(candidate)
                && !problem.initial.equals(candidate))
        ).forEach((candidate) -> {
            orphans.add(new Orphan(candidate, problem));
        });
        return orphans;
    }

    @Override
    public Set<Resolver> healers() {
        return set();
    }

    public static int count(Problem problem) {
        return (int) problem.plan.vertexSet().stream().filter((candidate)
                -> (problem.plan.outDegreeOf(candidate) == 0
                && !problem.goal.equals(candidate)
                && !problem.initial.equals(candidate))
        ).count();
    }

    public static Set<Orphan> related(Set<Action> children, Problem problem) {
        Set<Orphan> related = new HashSet<>();
        children.stream().filter((child) -> (problem.plan.outDegreeOf(child) == 0)).forEach((child) -> {
            related.add(new Orphan(child, problem));
        });
        return related;
    }

    @Override
    public String toString() {
        return "☹ " + needer;
    }

}
