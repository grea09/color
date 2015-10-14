/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package soda;

import soda.type.Problem;
import soda.exception.Failure;
import soda.type.Edge;
import soda.type.Action;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import me.grea.antoine.log.Log;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import soda.algorithm.POP;
import soda.algorithm.ProperPlan;

/**
 *
 * @author antoine
 */
public class PSPTest {

    /**
     * @param args the command line arguments
     * @throws soda.exception.Failure
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Action initial = new Action(null, set(1, 2));
        Action goal = new Action(set(3, 4, -5, 6), null);
        Set<Action> actions = set(new Action(set(1), set(3, 5)),
                new Action(set(5), set(4)),
                new Action(set(1), set(42)),
                new Action(set(2), set(-4)),
                new Action(set(42), set(3)),
                new Action(set(42), set(-5)),
                new Action(set(4), set(6))
        );
        DirectedGraph<Action, Edge> plan = ProperPlan.plan(goal, actions);
        plan.addVertex(initial);
        
        Problem problem = new Problem(initial, goal, actions, plan);

        System.out.println(problem);

        try {
            POP.solve(problem);
        } catch (Failure ex) {
            Log.e(ex);
            Log.out.flush();
            System.out.println("Partial solution {\n\t" + ex.partialSolution + "}");
            System.exit(-1);
        }
        
        System.out.println("Solution {\n\t" + problem.planToString() + "}");
    }

    public static <T> Set<T> set(T... list) {
        return new HashSet<>(Arrays.asList(list));
    }


}
