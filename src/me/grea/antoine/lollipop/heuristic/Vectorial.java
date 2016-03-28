/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.lollipop.heuristic;

import java.util.Comparator;
import me.grea.antoine.lollipop.type.Action;
import me.grea.antoine.lollipop.type.Problem;
import static java.lang.Math.sqrt;
import static java.lang.Math.abs;
import static java.lang.Math.pow;
import static java.lang.Math.abs;
import me.grea.antoine.utils.Log;

/**
 *
 * @author antoine
 */
public class Vectorial extends Heuristic {

    public Vectorial(Problem problem) {
        super(problem);
    }

    public static Comparator<Action> comparator(Problem problem) {
        return new Vectorial(problem).comparator();
    }

    @Override
    public double h(Action action) {
        int x = action.effects.size();
        int y = problem.domain.properPlan.outDegreeOf(action);
        int z = problem.plan.outDegreeOf(action);
        
        int x_ = action.preconditions.size();
        int y_ = problem.domain.properPlan.inDegreeOf(action);
        int z_ = problem.plan.inDegreeOf(action);
        
        Log.v(action + "=>" + (sqrt(pow(abs(-(y * x_) + x * y_), 2)) 
                + sqrt(pow(abs(z * x_ - x * z_), 2))
                + sqrt(pow(abs(-(z * y_) + y * z_), 2))));
        
        return sqrt(pow(abs(-(y * x_) + x * y_), 2)) 
                + sqrt(pow(abs(z * x_ - x * z_), 2))
                + sqrt(pow(abs(-(z * y_) + y * z_), 2));
    }

}
