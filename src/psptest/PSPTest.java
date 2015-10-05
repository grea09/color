/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package psptest;

import psptest.type.Problem;
import psptest.exception.Failure;
import psptest.type.Edge;
import psptest.type.Action;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import me.grea.antoine.log.Log;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import psptest.algorithm.PSP;

/**
 *
 * @author antoine
 */
public class PSPTest {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Failure {
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
        DirectedGraph<Action, Edge> plan = new DefaultDirectedGraph<>(Edge.class);
        plan.addVertex(initial);
        plan.addVertex(goal);
        
        Problem problem = new Problem(initial, goal, actions, plan);

        Log.i("Problem : " + problem);

        PSP.solve(problem);
        Log.i("Solution :" + problem.planToString());
    }

    public static <T> Set<T> set(T... list) {
        return new HashSet<>(Arrays.asList(list));
    }


}
