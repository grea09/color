/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.lollipop.type.flaw;

import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import me.grea.antoine.lollipop.type.Action;
import me.grea.antoine.lollipop.type.Problem;
import static me.grea.antoine.utils.Collections.queue;

/**
 *
 * @author antoine
 */
public class Orphan extends Flaw<Orphan> {

    private static Map<Action, Set<Action>> ghosts = new HashMap<>();

    public Orphan(Action orphan, Problem problem) {
        super(0, orphan, problem);
    }

    private Orphan(Problem problem) {
        super(problem);
    }

    @Override
    public Deque<Resolver> resolvers() {
        return queue(new Resolver(needer, null, 0, true));
    }

    public static void add(Action deadParent, Set<Action> orphans) {
        ghosts.put(deadParent, orphans); //2SPOOKY4U
    }

    public static Set<Orphan> related(Action troubleMaker, Problem problem) {
        return new Orphan(problem).related(troubleMaker);
    }

    @Override
    public Set<Orphan> related(Action troubleMaker) {
        Set<Orphan> related = new HashSet<>();
        if (troubleMaker.isSpecial()) {
            return related;
        }

        if (problem.plan.containsVertex(troubleMaker) && problem.plan.outDegreeOf(troubleMaker) == 0) {
            related.add(new Orphan(troubleMaker, problem));
        }
        if (ghosts.containsKey(troubleMaker)) {
            for (Action action : ghosts.get(troubleMaker)) {
                if (problem.plan.outDegreeOf(action) == 0) {
                    related.add(new Orphan(action, problem));
                }
            }
        }
        return related;
    }

    @Override
    public String toString() {
        return "☹ " + needer;
    }

}
