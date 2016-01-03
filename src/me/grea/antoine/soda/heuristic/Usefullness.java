/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.soda.heuristic;

import java.util.Comparator;
import me.grea.antoine.soda.type.Action;
import me.grea.antoine.soda.type.Problem;
import static java.lang.Math.round;

/**
 *
 * @author antoine
 */
public class Usefullness {

    public static double h(Problem problem, Action action) {
        if (problem.plan.containsVertex(action)) {
            return action.fake ? 0.0
                    : action == problem.goal
                    || action == problem.initial
                            ? Double.POSITIVE_INFINITY
                            : (problem.plan.outDegreeOf(action) * action.effects.size()) / (double) ((problem.plan.inDegreeOf(action) * action.preconditions.size()) + 0.1);
        }
        return h(action);
    }

    public static double h(Action action) {
        return action.fake ? 0.0
                : (action.effects.size()) / (double) ((action.preconditions.size()) + 0.1);
    }

    public static Comparator<Action> compare(Problem problem) {
        return (Action a1, Action a2) -> falseEquality(cast(h(problem, a1) - h(problem, a2)), a1, a2);
    }

    public static Comparator<Action> compare() {
        return (Action a1, Action a2) -> falseEquality(cast(h(a1) - h(a2)), a1, a2);
    }
    
    private static int falseEquality(int compare, Action a1, Action a2)
    {
        return a1.equals(a2) ? 0 : (compare == 0 && !a1.equals(a2)) ? a1.hashCode() - a2.hashCode(): compare;
    }

    private static int cast(double l) {
        if (l > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        } else if (l < Integer.MIN_VALUE) {
            return Integer.MIN_VALUE;
        }
        return (int) round(l);
    }
}
