/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.lollipop.heuristic;

import java.util.Comparator;
import me.grea.antoine.lollipop.type.Action;
import me.grea.antoine.lollipop.type.Problem;
import static java.lang.Math.pow;
import me.grea.antoine.utils.Log;

/**
 *
 * @author antoine
 */
public class Logarithmic extends Heuristic {

    public static double alpha = 2;

    public Logarithmic(Problem problem) {
        super(problem);
    }

    public static Comparator<Action> comparator(Problem problem) {
        return new Logarithmic(problem).comparator();
    }

    @Override
    public double h(Action action) {
        Log.d(action + "=>" + (action.effects.size() + problem.domain.properPlan.outDegreeOf(action) + problem.plan.outDegreeOf(action))
                / (pow(alpha, action.preconditions.size() + problem.domain.properPlan.inDegreeOf(action) + problem.plan.inDegreeOf(action))));
        
        return (action.effects.size() + problem.domain.properPlan.outDegreeOf(action) + problem.plan.outDegreeOf(action))
                / (pow(alpha, action.preconditions.size() + problem.domain.properPlan.inDegreeOf(action) + problem.plan.inDegreeOf(action)));
    }

}
