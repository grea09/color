/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.soda.algorithm;

import java.util.HashSet;
import java.util.Set;
import me.grea.antoine.log.Log;
import org.jgrapht.alg.CycleDetector;
import me.grea.antoine.soda.exception.Failure;
import me.grea.antoine.soda.type.Action;
import me.grea.antoine.soda.type.Edge;
import me.grea.antoine.soda.type.Problem;

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
        beUsefull(problem);
    }

    private static void illegal(Problem problem) {
        assert (problem.initial.preconditions.isEmpty());
        assert (problem.goal.effects.isEmpty());
        problem.actions.addAll(problem.plan.vertexSet()); //FIXME when instanciating you shouldn't do that

        for (Action action : problem.actions) {
            Set<Integer> effects = new HashSet<>(action.effects);
            effects.stream().filter((fluent) -> (action.effects.contains(-fluent))).forEach((fluent) -> {
                Log.w(action + " is illegal ! Contradiction fixed");
                action.effects.remove(fluent);
                action.effects.remove(-fluent);
            });
            Set<Integer> preconditions = new HashSet<>(action.preconditions);
            preconditions.stream().filter((fluent) -> (action.preconditions.contains(-fluent))).forEach((fluent) -> {
                Log.w(action + " is illegal ! Contradiction fixed");
                action.preconditions.remove(fluent);
                action.preconditions.remove(-fluent);
            });
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
        Set<Edge> concurents = new HashSet<>();
        for (Edge edge : problem.plan.edgeSet()) {
            //List<GraphPath<Action, Edge>> paths = new KShortestPaths<>(problem.plan, problem.plan.getEdgeSource(edge),2).getPaths(problem.plan.getEdgeTarget(edge));
            //if(paths.size() >1) {
            if ((Integer) edge.label == 0 && DFS.kReachable(problem.plan, problem.plan.getEdgeSource(edge), problem.plan.getEdgeTarget(edge), 2)) {
                Log.w("The edge " + edge + " is redudant");// with " + paths.get(1));
                problem.plan.removeEdge(edge);
                dontRepeat(problem);
                return;
            }
            
            for(Edge concurent :problem.plan.incomingEdgesOf(problem.plan.getEdgeTarget(edge)))
            {
                if(concurent != edge && !concurents.contains(edge) && !concurents.contains(concurent)
                        && concurent.label == edge.label)
                {
                    Log.w("The edge " + concurent + " is competing with " + edge);
                    concurents.add(concurent);
                    concurents.remove(edge);
                }
            }
        }
        problem.plan.removeAllEdges(concurents);
    }

    private static void beUsefull(Problem problem) {
        HashSet<Action> actions = new HashSet<>(problem.actions);
        for (Action action : actions) {
            if (action.effects.isEmpty() && action != problem.goal) {
                Log.w("Useless action " + action + " removed !");
                problem.actions.remove(action);
                problem.plan.removeVertex(action);
            }
            if(problem.plan.containsVertex(action) && problem.plan.outDegreeOf(action) == 0 && 
                    action != problem.goal && action != problem.initial)
            {
                Log.w("Useless action " + action + " removed !");
                problem.plan.removeVertex(action);
            }
        }
        
        //TODO moar
    }
}
