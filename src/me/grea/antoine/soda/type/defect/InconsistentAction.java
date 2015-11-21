/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.soda.type.defect;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import me.grea.antoine.log.Log;
import me.grea.antoine.soda.type.Action;
import me.grea.antoine.soda.type.Edge;
import me.grea.antoine.soda.type.Problem;
import static me.grea.antoine.soda.utils.Collections.set;
import static me.grea.antoine.soda.utils.Collections.union;

/**
 *
 * @author antoine
 */
public class InconsistentAction extends Defect {

    public Action inconsistent;
    public Set<Integer> preconditions;
    public Set<Integer> effects;

    public InconsistentAction(Action inconsistent, Set<Integer> preconditions, Set<Integer> effects, Problem problem) {
        super(problem);
        this.inconsistent = inconsistent;
        this.preconditions = preconditions;
        this.effects = effects;
    }

    @Override
    public void fix() {
        Set<Integer> outGoing = set();
        if (problem.plan.containsVertex(inconsistent)) {
            for (Edge edge : problem.plan.outgoingEdgesOf(inconsistent)) {
                outGoing.addAll(edge.labels);
            }
        }
        Set<Integer> inComing = set();
        if (problem.plan.containsVertex(inconsistent)) {
            for (Edge edge : problem.plan.incomingEdgesOf(inconsistent)) {
                inComing.addAll(edge.labels);
            }
        }

        for (int precondition : preconditions) {
            if (inComing.contains(precondition) && inComing.contains(-precondition)) {
                inconsistent.preconditions.remove(precondition);
                inComing.remove(precondition);
            } else if (inComing.contains(precondition)) {
                inconsistent.preconditions.remove(-precondition);
                inComing.remove(-precondition);
            } else {
                inconsistent.preconditions.remove(precondition);
                inComing.remove(precondition);
                inconsistent.preconditions.remove(-precondition);
                inComing.remove(-precondition);
            }
        }
        for (int effect : effects) {
            if (inComing.contains(effect) && inComing.contains(-effect)) {
                inconsistent.effects.remove(effect);
                inComing.remove(effect);
            } else if (inComing.contains(effect)) {
                inconsistent.effects.remove(-effect);
                inComing.remove(-effect);
            } else {
                inconsistent.effects.remove(effect);
                inComing.remove(effect);
                inconsistent.effects.remove(-effect);
                inComing.remove(-effect);
            }
        }
    }

    public static Set<InconsistentAction> find(Problem problem) {
        Set<InconsistentAction> result = new HashSet<>();
        for (Action action : union(problem.actions, problem.plan.vertexSet())) {
            InconsistentAction inconsistent = is(action, problem);
            if (inconsistent != null) {
                result.add(inconsistent);
            }
        }
        return result;
    }

    public static InconsistentAction is(Action action, Problem problem) {
        Set<Integer> effects = new HashSet<>();
        for (int fluent : action.effects) {
            if (action.effects.contains(-fluent)) {
                effects.add(fluent);
            }
        }

        Set<Integer> precondition = new HashSet<>();
        for (int fluent : action.effects) {
            if (action.effects.contains(-fluent)) {
                effects.add(fluent);
            }
        }

        if (effects.size() + precondition.size() > 0) {
            return new InconsistentAction(action, precondition, effects, problem);
        }
        return null;
    }

    @Override
    public String toString() {
        return "Action " + inconsistent + " is inconsistent as it has " +
                (preconditions.isEmpty() ? '[' : "[ ⊧" + preconditions) + 
                (effects.isEmpty() ? ']' : " +" + effects + ']');
    }

}
