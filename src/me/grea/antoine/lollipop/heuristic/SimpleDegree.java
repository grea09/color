/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.lollipop.heuristic;

import java.util.Comparator;
import me.grea.antoine.lollipop.type.Action;
import me.grea.antoine.lollipop.type.Problem;
import me.grea.antoine.utils.Log;

/**
 *
 * @author antoine
 */
public class SimpleDegree extends Heuristic{

    public SimpleDegree(Problem problem) {
        super(problem);
    }
    
    public static Comparator<Action> comparator(Problem problem) {
        return new SimpleDegree(problem).comparator();
    }

    @Override
    public double h(Action action) {
        Log.d(action + "=>" + (action.effects.size() - action.preconditions.size()) * 
                (problem.domain.properPlan.outDegreeOf(action) - problem.domain.properPlan.inDegreeOf(action)) *
                (problem.plan.outDegreeOf(action) - problem.plan.inDegreeOf(action)));
        
        
        return (action.effects.size() - action.preconditions.size()) * 
                (problem.domain.properPlan.outDegreeOf(action) - problem.domain.properPlan.inDegreeOf(action)) *
                (problem.plan.outDegreeOf(action) - problem.plan.inDegreeOf(action));
    }
    
}
