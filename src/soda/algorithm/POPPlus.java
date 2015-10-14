/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package soda.algorithm;

import java.util.Map;
import me.grea.antoine.log.Log;
import org.jgrapht.DirectedGraph;
import soda.PSPTest;
import soda.exception.Failure;
import soda.type.Action;
import soda.type.Problem;
import soda.type.Edge;
import soda.type.Problem.Flaw;
import soda.type.Problem.Threat;

/**
 *
 * @author antoine
 */
public class POPPlus {

    public static void soft(Problem problem) {
        double minViolation = Double.POSITIVE_INFINITY;
        Problem best = problem;
        Flaw annoyer = null;
        for (Map.Entry<Flaw, DirectedGraph<Action, Edge>> fail : problem.partialSolutions.entrySet()) {
            Problem partial = new Problem(problem);
            partial.plan = fail.getValue();
            partial.partialSolutions = problem.partialSolutions;
//            Log.d("Considering : " + partial.planToString());
            double currentViolation = violation(partial);
//            Log.d("Violation = " +currentViolation);
            if (currentViolation < minViolation) {
                best = partial;
                annoyer = fail.getKey();
                minViolation = currentViolation;
            }
        }
        Log.i("Best partial " +best.planToString());
        Log.i("Violation : " + minViolation);
        POPMinus.clean(best);
        problem.plan = best.plan;
        problem.partialSolutions.clear();
        problem.quarantine.clear();
        fix(annoyer, problem);
        Log.i("Trying again !");
        POP.solve(problem);
    }

    private static long violation(Problem partial) {
        return partial.suboals().size() + partial.threats().size() + partial.violation();
    }
    
    private static void fix(Flaw flaw, Problem problem)
    {
        Action provider = new Action(null, PSPTest.set(flaw.fluent), true);
        Log.i("Fixing flaw " + flaw + " with fake action " + provider);
        problem.plan.addVertex(provider);
        Edge edge = problem.plan.addEdge(provider, flaw.needer);
        edge.label = flaw.fluent;
        
        if(flaw instanceof Threat)
        {
            Threat threat = (Threat) flaw;
            edge = problem.plan.addEdge(threat.breaker, provider); 
            edge.label = 0;
        }
    }

}
