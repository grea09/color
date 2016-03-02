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
    
    public Set<Orphan> related(Action troubleMaker) {
        Set<Orphan> related = new HashSet<>();
//        problem.plan.outDegreeOf(child) == 0
        return related;
    }

    @Override
    public String toString() {
        return "☹ " + needer;
    }

}
