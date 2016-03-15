/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.lollipop.heuristic;

import java.util.Comparator;
import me.grea.antoine.lollipop.type.Action;
import me.grea.antoine.lollipop.type.Problem;

/**
 *
 * @author antoine
 */
public class Scalaire extends Heuristic {

    public Scalaire(Problem problem) {
        super(problem);
    }

    public static Comparator<Action> comparator(Problem problem) {
        return new Scalaire(problem).comparator();
    }

    @Override
    public double h(Action action) { 
        return (action.effects.size() * -action.preconditions.size()) 
                + (problem.domain.operatorGraph.outDegreeOf(action) * -problem.domain.operatorGraph.inDegreeOf(action)) 
                + (problem.plan.outDegreeOf(action) * -problem.plan.inDegreeOf(action));
    }

}
