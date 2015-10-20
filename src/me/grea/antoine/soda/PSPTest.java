/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.soda;

import me.grea.antoine.soda.type.Problem;
import me.grea.antoine.soda.exception.Failure;
import me.grea.antoine.soda.type.Edge;
import me.grea.antoine.soda.type.Action;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import me.grea.antoine.log.Log;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import me.grea.antoine.soda.algorithm.POP;
import me.grea.antoine.soda.algorithm.ProperPlan;

/**
 *
 * @author antoine
 */
public class PSPTest {

    /**
     * @param args the command line arguments
     * @throws me.grea.antoine.soda.exception.Failure
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
                new Action(set(4), set(6)),
                new Action(set(-7, 8), set(-8, 7)),
                new Action(set(7, -8), set(8, -7))
        );
        DirectedGraph<Action, Edge> plan = ProperPlan.plan(goal, actions);
        plan.addVertex(initial);
        
        Problem problem = new Problem(initial, goal, actions, plan);

        System.out.println(problem);

        POP.solve(problem);
        
        
        Log.i("Finih !");
        Log.out.println("Solution {\n\t" + problem.planToString() + "}");
        Log.out.println("Violation : " + problem.violation());
    }

    public static <T> Set<T> set(T... list) {
        return new HashSet<>(Arrays.asList(list));
    }


}
