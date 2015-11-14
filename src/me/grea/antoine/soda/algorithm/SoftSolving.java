/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.soda.algorithm;

import java.util.HashSet;
import java.util.Map;
import me.grea.antoine.log.Log;
import org.jgrapht.DirectedGraph;
import me.grea.antoine.soda.Main;
import me.grea.antoine.soda.type.Action;
import me.grea.antoine.soda.type.Edge;
import me.grea.antoine.soda.type.Flaw;
import me.grea.antoine.soda.type.Problem;
import me.grea.antoine.soda.type.Resolver;
import me.grea.antoine.soda.type.SubGoal;
import me.grea.antoine.soda.type.Threat;
import static me.grea.antoine.soda.utils.Collections.*;

/**
 *
 * @author antoine
 */
public class SoftSolving {

    public static void heal(Problem problem) {
        double minViolation = Double.POSITIVE_INFINITY; // double for infinity
        DirectedGraph<Action, Edge> best = problem.plan;
        Flaw annoyer = null;
        for (Map.Entry<Flaw, DirectedGraph<Action, Edge>> fail : problem.partialSolutions.entrySet()) {
            Log.v("Considering : " + fail);
            double currentViolation = violation(fail.getValue(), problem.goal);
            Log.d("Violation = " +currentViolation);
            if (currentViolation < minViolation) {
                best = fail.getValue();
                annoyer = fail.getKey();
                minViolation = currentViolation;
            }
        }
        problem.plan = best;
        Log.i("Best partial " +problem.planToString());
        Log.i("Violation : " + minViolation);
        problem.partialSolutions.clear();
        for(Resolver resolver : annoyer.healers() ) { resolver.apply(best); };
        Log.i("Plan healed !");
    }

    private static long violation(DirectedGraph<Action, Edge> partial, Action goal) {
        return SubGoal.count(partial, goal) + Threat.count(partial) + 
                (partial.vertexSet().stream().filter((action) -> (action.fake)).count());
    }
    

}
