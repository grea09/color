/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.soda.type.defect;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import me.grea.antoine.soda.type.Action;
import me.grea.antoine.soda.type.Problem;
import static me.grea.antoine.soda.utils.Collections.union;
import static me.grea.antoine.soda.utils.Collections.intersection;

/**
 *
 * @author antoine
 */
public class ToxicAction extends Defect {

    Action toxic;
    Set<Integer> effects;

    public ToxicAction(Action toxic, Set<Integer> effects, Problem problem) {
        super(problem);
        this.toxic = toxic;
        this.effects = effects;
    }

    @Override
    public void fix() {
        if(toxic.effects.containsAll(effects))
        {
            problem.plan.removeVertex(toxic);
            problem.actions.remove(toxic);
        }
        toxic.effects.removeAll(effects);
    }

    public static Set<ToxicAction> find(Problem problem) {
        Set<ToxicAction> result = new HashSet<>();
        for (Action action : union(problem.actions, problem.plan.vertexSet())) {
            ToxicAction toxic = is(action, problem);
            if (toxic != null) {
                result.add(toxic);
            }
        }
        return result;
    }

    public static ToxicAction is(Action action, Problem problem) {
        Set<Integer> effects = intersection(action.preconditions, action.effects);
        if (!(action.effects.isEmpty() && action != problem.goal) && effects.isEmpty()) {
            return null;
        }
        return new ToxicAction(action, effects, problem);
    }

    @Override
    public String toString() {
        return "Action " + toxic + " has the toxic effects " + effects;
    }

}
