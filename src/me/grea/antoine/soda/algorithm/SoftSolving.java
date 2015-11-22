/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.soda.algorithm;

import me.grea.antoine.log.Log;
import me.grea.antoine.soda.type.PartialSolution;
import me.grea.antoine.soda.type.flaw.Flaw;
import me.grea.antoine.soda.type.Plan;
import me.grea.antoine.soda.type.Problem;
import me.grea.antoine.soda.type.flaw.Resolver;

/**
 *
 * @author antoine
 */
public class SoftSolving {

    public static void heal(Problem problem) {
        double minViolation = Double.POSITIVE_INFINITY; // double for infinity
        Plan best = problem.plan;
        Flaw annoyer = null;
        for (PartialSolution partialSolution : problem.partialSolutions) {
            double currentViolation = partialSolution.remaining.size() +
                    (partialSolution.plan.vertexSet().stream().filter((action) -> (action.fake)).count());
            if (currentViolation < minViolation) {
                best = partialSolution.plan;
                annoyer = partialSolution.cause;
                minViolation = currentViolation;
            }
        }
        problem.plan = best;
        Log.i("Violation : " + minViolation);
        problem.partialSolutions.clear();
        for(Resolver resolver : annoyer.healers() ) { resolver.apply(best); };
        Log.i("Plan healed !");
    }
    

}
