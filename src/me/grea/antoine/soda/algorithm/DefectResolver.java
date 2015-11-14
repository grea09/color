/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.soda.algorithm;

import me.grea.antoine.soda.utils.DFS;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import me.grea.antoine.log.Log;
import org.jgrapht.alg.CycleDetector;
import me.grea.antoine.soda.exception.Failure;
import me.grea.antoine.soda.type.Action;
import me.grea.antoine.soda.type.Edge;
import me.grea.antoine.soda.type.Problem;
import static me.grea.antoine.soda.utils.Collections.*;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.KShortestPaths;

/**
 *
 * @author antoine
 */
public class DefectResolver {

    public static void clean(Problem problem) {
        illegal(problem);
        beUsefull(problem);
        lieToMe(problem);
//        breakCycles(problem);
        dontRepeat(problem);
        beUsefull(problem);
    }

    private static void illegal(Problem problem) {
        assert (problem.initial.preconditions.isEmpty());
        assert (problem.goal.effects.isEmpty());
        problem.plan.addVertex(problem.initial);
        problem.plan.addVertex(problem.goal);
        Set<Action> allActions = union(problem.actions, problem.plan.vertexSet());

        for (Action action : allActions) {
            Set<Integer> effects = new HashSet<>(action.effects);
            effects.stream().filter((fluent) -> (fluent > 0 && action.effects.contains(-fluent))).forEach((fluent) -> {
                Log.w(action + " is illegal ! Contradiction fixed");
                Set<Integer> linked = set();
                if (problem.plan.containsVertex(action)) {
                    for (Edge edge : problem.plan.outgoingEdgesOf(action)) {
                        linked.addAll(edge.labels);
                    }
                    if (linked.contains(fluent)) {
                        action.effects.remove(-fluent);
                    } else if (linked.contains(-fluent)) {
                        action.effects.remove(fluent);
                    } else {
                        action.effects.remove(fluent);
                        action.effects.remove(-fluent); //TODO see consequences
                    }
                } else {
                    action.effects.remove(fluent);
                    action.effects.remove(-fluent); //TODO see consequences
                }
            });
            
            Set<Integer> preconditions = new HashSet<>(action.preconditions);
            preconditions.stream().filter((fluent) -> (fluent > 0 && action.preconditions.contains(-fluent))).forEach((fluent) -> {
                Log.w(action + " is illegal ! Contradiction fixed");
                Set<Integer> linked = set();
                for (Edge edge : problem.plan.incomingEdgesOf(action)) {
                    linked.addAll(edge.labels);
                }
                if (linked.contains(fluent)) {
                    action.preconditions.remove(-fluent);
                } else if (linked.contains(-fluent)) {
                    action.preconditions.remove(fluent);
                } else {
                    action.preconditions.remove(fluent);
                    action.preconditions.remove(-fluent); //TODO see consequences
                }
            });
            
            if(action.effects.containsAll(action.preconditions) && !action.preconditions.isEmpty()
                    && action != problem.goal && action != problem.initial)
            { // BUG in Jgrapht
                Log.w("Toxic action " + action + " removed !");
                problem.actions.remove(action);
                problem.plan.removeVertex(action);
                continue;
            }
            if(action.effects.removeAll(action.preconditions))
            {
                Log.w("Action " + action + " has been cleaned !");
            }
        }
    }

    private static void lieToMe(Problem problem) {
        Set<Edge> edges = problem.plan.edgeSet();
        for (Edge edge : edges) {
            Set<Integer> common = union(
                    problem.plan.getEdgeSource(edge).effects,
                    problem.plan.getEdgeTarget(edge).preconditions);
            Set<Integer> lies = difference(edge.labels, common);
            if (!lies.isEmpty()) {
                Log.w(edge + " lied to me about " + lies);
                Set<Integer> saviours = difference(common, edge.labels);
                if (saviours.isEmpty()) {
                    edge.labels.removeAll(lies);
                    if (edge.labels.isEmpty()) {
                        Log.w(edge + " removed from plan.");
                        problem.plan.removeEdge(edge);
                    }
                } else {
                    Log.w(edge + " saved by " + saviours);
                    edge.labels.removeAll(lies);
                    edge.labels.addAll(saviours);
                }
            }
        }
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
                } //FIXME improve on cutting
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
            for (Edge concurent : problem.plan.incomingEdgesOf(problem.plan.getEdgeTarget(edge))) {
                if (concurent != edge && !concurents.contains(edge) && !concurents.contains(concurent)
                        && concurent.labels.containsAll(edge.labels) 
                        && !edge.labels.isEmpty() && !concurent.labels.isEmpty()
                        && problem.plan.outDegreeOf(problem.plan.getEdgeSource(concurent)) 
                            <= problem.plan.outDegreeOf(problem.plan.getEdgeSource(edge))) {
                    Log.w("The edge " + concurent + " is competing with " + edge); //FIXME
                    concurents.add(concurent);
                    concurents.remove(edge);
                }
            }
            
            if(edge.labels.isEmpty())
            {
                List<GraphPath<Action, Edge>> paths = new KShortestPaths<>(problem.plan, problem.plan.getEdgeSource(edge),2).getPaths(problem.plan.getEdgeTarget(edge));
                if(paths.size() >1) {
    //            if (edge.labels.isEmpty() && DFS.kReachable(problem.plan, problem.plan.getEdgeSource(edge), problem.plan.getEdgeTarget(edge), 2)) {
                    Log.w("The edge " + edge + " is redudant with " + paths.get(1));
                    problem.plan.removeEdge(edge);
                    dontRepeat(problem);
                    return;
                }
            }
        }
        problem.plan.removeAllEdges(concurents);
    }

    private static void beUsefull(Problem problem) {
        HashSet<Action> actions = new HashSet<>(problem.actions);
        for (Action action : actions) {
            if (action.effects.isEmpty()
                    && action != problem.goal && action != problem.initial) { //not providing
                Log.w("Useless action " + action + " removed !");
                problem.actions.remove(action);
                problem.plan.removeVertex(action);
            }
            if (problem.plan.containsVertex(action) && problem.plan.outDegreeOf(action) == 0
                    && action != problem.goal && action != problem.initial) { //oprhan
                Log.w("Useless action " + action + " removed !");
                problem.plan.removeVertex(action);
            }
        }

        //TODO moar
    }
}
