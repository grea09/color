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

/**
 *
 * @author antoine
 */
public class Pearson extends Heuristic {

    public Pearson(Problem problem) {
        super(problem);
    }

    public static Comparator<Action> comparator(Problem problem) {
        return new Pearson(problem).comparator();
    }

    @Override
    public double h(Action action) {
        int x = action.effects.size();
        int y = problem.domain.operatorGraph.outDegreeOf(action);
        int z = problem.plan.outDegreeOf(action);
        
        int x_ = action.preconditions.size();
        int y_ = problem.domain.operatorGraph.inDegreeOf(action);
        int z_ = problem.plan.inDegreeOf(action);
        
        return ((x*x_ + y*y_ + z*z_) - (x+y+z)*(x_+y_+z_))
                / (sqrt(3*(x*x+y*y+z*z)-pow(x+y+z,2)) * sqrt(3*(x_*x_+y_*y_+z_*z_)-pow(x_+y_+z_,2)));
    }

}
