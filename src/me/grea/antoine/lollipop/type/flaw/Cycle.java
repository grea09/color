/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.lollipop.type.flaw;

import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;
import me.grea.antoine.lollipop.type.Action;
import me.grea.antoine.lollipop.type.Problem;
import static me.grea.antoine.utils.Collections.queue;
import static me.grea.antoine.utils.Collections.set;

/**
 *
 * @author antoine
 */
public class Cycle extends Flaw<Cycle> {

    private Set<Action> closedWalk;

    private Cycle(Problem problem) {
        super(problem);
    }

    public Cycle(Set<Action> closedWalk, Problem problem) {
        super(0, closedWalk.iterator().next(), problem);
        this.closedWalk = closedWalk;
    }

    @Override
    public Deque<Resolver> resolvers() {
        Action lessUsefull = Collections.min(closedWalk, problem.heuristic.comparator());
        for (Action action : closedWalk) {
            if (problem.plan.containsEdge(lessUsefull, action)) {
                return queue(new Resolver(lessUsefull, action, 0, true));
            }
        }
        return queue();
    }

    public static Set<Cycle> toCycles(Set<Set<Action>> connectedSets, Problem problem) {
        return new HashSet<Cycle>() {
            {
                for (Set<Action> component : connectedSets) {
                    if (component.size() > 1 && problem.plan.vertexSet().containsAll(component)) {
                        add(new Cycle(component, problem));
                    } else {
                        Action action = component.iterator().next();
                        if (problem.plan.containsEdge(action, action)) {
                            add(new Cycle(set(action), problem));
                        }
                    }
                }
            }
        };
    }

    @Override
    public Set<Cycle> related(Action troubleMaker) {
        return new HashSet(); // Because no cycle can be related
    }

    @Override
    public String toString() {
        return "⟳ " + closedWalk;
    }

}
