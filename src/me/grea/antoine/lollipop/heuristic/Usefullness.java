/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.lollipop.heuristic;

import static java.lang.Math.pow;
import static java.lang.Math.round;
import java.util.Comparator;
import me.grea.antoine.lollipop.type.Action;
import me.grea.antoine.lollipop.type.Problem;

/**
 *
 * @author antoine
 */
public class Usefullness {

    public static double h(Problem problem, Action action) {
        if (problem.plan.containsVertex(action)) {
            return (problem.plan.outDegreeOf(action) + action.effects.size()) / (double) pow(2, (problem.plan.inDegreeOf(action) + action.preconditions.size()));
        }
        return h(action);
    }

    public static double h(Action action) {
        return (action.effects.size()) / (double) pow(2, action.preconditions.size());
    }

    public static Comparator<Action> compare(Problem problem) {
        return (Action a1, Action a2) -> {
            if (a1.equals(a2)) {
                return 0;
            }
            int value = cast(h(problem, a1) - h(problem, a2));
            if ((a1.fake && a2.fake)
                    || (isSpecial(problem, a1) && isSpecial(problem, a2))
                    || (value == 0)) {
                return 0;//a1.hashCode() - a2.hashCode();
            } else if (isSpecial(problem, a1)) {
                return 1;
            } else if (isSpecial(problem, a2)) {
                return -1;
            } else if (a1.fake) {
                return -1;
            } else if (a2.fake) {
                return 1;
            }

            return value;
        };
    }

    public static Comparator<Action> compare() {
        return (Action a1, Action a2) -> {
            if (a1.equals(a2)) {
                return 0;
            }
            int value = cast(h(a1) - h(a2));
            if ((a1.fake && a2.fake)
                    || (value == 0)) {
                return a1.hashCode() - a2.hashCode();
            } else if (a1.fake) {
                return -1;
            } else if (a2.fake) {
                return 1;
            }

            return value;
        };
    }

    private static boolean isSpecial(Problem problem, Action action) {
        return action == problem.goal || action == problem.initial;
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
