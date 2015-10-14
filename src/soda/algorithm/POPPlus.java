/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package soda.algorithm;

import java.util.Map;
import java.util.Set;
import me.grea.antoine.log.Log;
import org.jgrapht.DirectedGraph;
import soda.exception.Failure;
import soda.type.Action;
import soda.type.Problem;
import soda.type.Edge;
import soda.type.Problem.Flaw;
import soda.type.Problem.SubGoal;
import soda.type.Problem.Threat;

/**
 *
 * @author antoine
 */
public class POPPlus {

    public static void soft(Problem problem) throws Failure {
        double minViolation = Double.POSITIVE_INFINITY;
        Problem best = problem;
        Flaw annoyer = null;
        for (Map.Entry<Flaw, DirectedGraph<Action, Edge>> fail : problem.partialSolutions.entrySet()) {
            Problem partial = new Problem(problem);
            partial.plan = fail.getValue();
            partial.partialSolutions = problem.partialSolutions;
            Log.d("Considering : " + partial.planToString());
            double currentViolation = violation(partial);
            Log.d("Violation = " +currentViolation);
            if (currentViolation < minViolation) {
                best = partial;
                annoyer = fail.getKey();
                minViolation = currentViolation;
            }
        }
        Log.d("Best partial " +best.planToString());
        POPMinus.clean(best);
        solve(best);
        POPMinus.clean(best);
        if(problem.plan.equals(best.plan))
        {
            throw new Failure(best, annoyer);
        }
        soft(best);
    }

    private static double violation(Problem partial) {
        return partial.suboals().size() + partial.threats().size();// / (double) partial.plan.edgeSet().size();
    }
    
     private static void satisfy(Problem problem) {
        Set<SubGoal> subgoals = problem.suboals();
        for (SubGoal subGoal : subgoals) {
            if (problem.initial.effects.contains(subGoal.subgoal)) {
                subGoal.satisfy(problem.initial);
            }

            for (Action step : problem.plan.vertexSet()) {
                if (step.effects.contains(subGoal.subgoal)) {
                    subGoal.satisfy(step);
                }
            }
            for (Action action : problem.actions) {
                if (action.effects.contains(subGoal.subgoal)) {
                    subGoal.satisfy(action);
                }
            }
        }
    }

    private static void unthreaten(Problem problem) {
       for(Threat threat : problem.threats())
       {
           try {
               threat.demote();
           } catch (Failure ex) {
               try {
                   threat.promote();
               } catch (Failure ex1) {
                   
               }
           }
       }
    }

    private static void solve(Problem problem) {
        satisfy(problem);
        unthreaten(problem);
    }

}
