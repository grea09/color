/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.soda.type.defect;

import java.util.Deque;
import me.grea.antoine.soda.type.Action;
import me.grea.antoine.soda.type.Problem;
import me.grea.antoine.soda.type.flaw.Resolver;

/**
 *
 * @author antoine
 */
public abstract class Defect {

    public final Problem problem;

    public Defect(Problem problem) {
        this.problem = problem;
    }

    public abstract void fix();

    public static double usefullness(Action action, Problem problem) {
        return action.fake ? 0.0
                : action == problem.goal
                || action == problem.initial
                || problem.plan.inDegreeOf(action) == 0
                ? Double.POSITIVE_INFINITY
                : problem.plan.outDegreeOf(action) / (double) problem.plan.inDegreeOf(action);
    }

    @Override
    public abstract String toString();

}
