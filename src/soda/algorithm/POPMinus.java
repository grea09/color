/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package soda.algorithm;

import java.util.HashSet;
import java.util.Set;
import me.grea.antoine.log.Log;
import org.jgrapht.alg.CycleDetector;
import soda.exception.Failure;
import soda.type.Action;
import soda.type.Edge;
import soda.type.Problem;

/**
 *
 * @author antoine
 */
public class POPMinus {

    public static void clean(Problem problem){
        illegal(problem);
        lieToMe(problem);
        breakCycles(problem);
        dontRepeat(problem);
    }

    private static void illegal(Problem problem) {
        assert (problem.initial.preconditions.isEmpty());
        assert (problem.goal.effects.isEmpty());
        problem.actions.addAll(problem.plan.vertexSet()); //FIXME when instanciating you shouldn't do that

        for (Action action : problem.actions) {
            if (action.effects.contains(0) || action.preconditions.contains(0)
                    || (action.effects.isEmpty() && action != problem.goal)) {
                Log.w("Illegal action " + action + " removed !");
                problem.actions.remove(action);
                problem.plan.removeVertex(action);
            }
        }
    }

    private static void lieToMe(Problem problem) {
        Set<Edge> liars = new HashSet<>();
        for (Edge edge : problem.plan.edgeSet()) {
            if ((Integer) edge.label != 0 && (!problem.plan.getEdgeSource(edge).effects.contains(edge.label)
                    || !problem.plan.getEdgeTarget(edge).preconditions.contains(edge.label))) {
                Log.w(edge + " lied to me ! Removed from plan now.");
                liars.add(edge);
            }
        }
        problem.plan.removeAllEdges(liars);
    }

    private static void breakCycles(Problem problem) {
        Set<Action> cycles = new CycleDetector<>(problem.plan).findCycles();
        if (cycles.isEmpty()) {
            return;
        }
        Log.w("There is a cycle ! These actions are looping : " + cycles);
        Set<Action> closed = new HashSet<>();
        for (Action a1 : cycles) {
            closed.add(a1);
            for (Action a2 : cycles) {
                if (closed.contains(a2)) {
                    continue;
                }
                if (problem.plan.containsEdge(a1, a2)) {
                    Log.d("Cycle cut between " + a1 + " and " + a2);
                    problem.plan.removeAllEdges(a1, a2);
                    breakCycles(problem);
                    return;
                }
            }
        }
    }

    private static void dontRepeat(Problem problem) {
        for (Edge edge : problem.plan.edgeSet()) {
            //List<GraphPath<Action, Edge>> paths = new KShortestPaths<>(problem.plan, problem.plan.getEdgeSource(edge),2).getPaths(problem.plan.getEdgeTarget(edge));
            //if(paths.size() >1) {
            if (DFS.kReachable(problem.plan, problem.plan.getEdgeSource(edge), problem.plan.getEdgeTarget(edge), 2)) {
                Log.w("The edge " + edge + " is redudant");// with " + paths.get(1));
                problem.plan.removeEdge(edge);
                dontRepeat(problem);
                return;
            }
        }
    }
}
