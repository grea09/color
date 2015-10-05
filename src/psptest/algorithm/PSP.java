/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package psptest.algorithm;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.grea.antoine.log.Log;
import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.graph.DefaultEdge;
import psptest.type.Action;
import psptest.type.Edge;
import psptest.exception.Failure;
import psptest.type.Problem;
import psptest.exception.Success;

/**
 *
 * @author antoine
 */
public class PSP {

    public static void solve(Problem problem) throws Failure {
        try {
            assert (problem.initial.preconditions.isEmpty());
            assert (problem.goal.effects.isEmpty());

            solve_(problem);
            throw new Failure(null);
        } catch (Success s) {
            //TODO add plan building
            s.printStackTrace(Log.out);
        }
    }

    private static void solve_(Problem problem) throws Success {
        try {
            unthreaten(problem);
            satisfy(problem);
            throw new Success();
        } catch (Failure ex) {
            Log.e(ex);
            return;
        }
    }

    private static void satisfy(Problem problem) throws Success, Failure {
        Map<Integer, Action> subgoals = subgoal(problem);
        Log.d("Subgoals : " + subgoals);
        for (Map.Entry<Integer, Action> subgoal : subgoals.entrySet()) {
            if (problem.initial.effects.contains(subgoal.getKey())) {
                insert(problem, subgoal.getKey(), subgoal.getValue(), problem.initial);
            }

            for (Action step : problem.plan.vertexSet()) {
                if (step.effects.contains(subgoal.getKey())) {
                    insert(problem, subgoal.getKey(), subgoal.getValue(), step);
                }
            }
            for (Action action : problem.actions) {
                if (action.effects.contains(subgoal.getKey())) {
                    insert(problem, subgoal.getKey(), subgoal.getValue(), action);
                }
            }
            throw new Failure((Object) subgoal);
        }
    }

    private static Map<Integer, Action> subgoal(Problem problem) {
        Map<Integer, Action> subgoals = new HashMap<>();
        Deque<Action> open = new ArrayDeque<>(Arrays.asList(problem.goal));
        while (!open.isEmpty()) {
            Action current = open.pollLast();
//            for (int effect : current.effects) {
//                subgoals.remove(effect);
//            }
            for (int precondition : current.preconditions) {
//                if (!problem.initial.effects.contains(precondition)) {
                subgoals.put(precondition, current);
//                }
            }
            for (Edge edge : problem.plan.incomingEdgesOf(current)) {
                subgoals.remove(edge.label);
                open.addFirst(problem.plan.getEdgeSource(edge));
            }
        }
        return subgoals;
    }

    private static void insert(Problem problem, int subgoal, Action toSatisfy, Action candidate) throws Success {
        Log.d("Inserting " + candidate + " to satisfy precondition " + subgoal + " of goal " + toSatisfy);
        //TODO Fail if negative effects
        problem.plan.addVertex(candidate);
        Edge edge = problem.plan.addEdge(candidate, toSatisfy);
        edge.label = subgoal;

        solve_(problem);

        Log.d("Action " + candidate + " not suited, reverting");
        problem.plan.removeEdge(candidate, toSatisfy);
        if (problem.plan.outDegreeOf(candidate) == 0) {
            problem.plan.removeVertex(candidate);
        }
    }

    private static void unthreaten(Problem problem) throws Success, Failure { //TODO ignore initial and goal
        for (Action candidate : problem.plan.vertexSet()) {
            for (int effect : candidate.effects) {
                for (Edge oposite : problem.plan.edgeSet()) {
                    if ((Integer) oposite.label == -effect) { //NEVER have a 0 effect
                        Action source = problem.plan.getEdgeSource(oposite);
                        Action target = problem.plan.getEdgeTarget(oposite);
                        if (problem.plan.getEdge(candidate, source) == null
                                && problem.plan.getEdge(target, candidate) == null) {
                            Log.w("" + candidate + " is a threat to the link " + source + " => " + target);
                            demote(problem, source, target, candidate, effect);
                            promote(problem, source, target, candidate, effect);
                            throw new Failure(candidate);
                        }
                    }
                }
            }
        }
    }

    private static void demote(Problem problem, Action source, Action target, Action threat, int effect) throws Success {
        Log.d("Demoting " + threat + " in the link " + problem.plan.getEdge(source, target));
        if (target == problem.goal) {
            Log.w("Can't demote after goal step !");
            return;
        }
        Edge edge;
        edge = problem.plan.addEdge(target, threat);
        edge.label = 0;
        // TODO consistency check
        if (!new CycleDetector<>(problem.plan).detectCycles()) {
            solve_(problem);
        }
        // Revert !
        problem.plan.removeEdge(edge);
    }

    private static void promote(Problem problem, Action source, Action target, Action threat, int effect) throws Success {
        Log.d("Promoting " + threat + " in the link " + problem.plan.getEdge(source, target));
        if (source == problem.initial) {
            Log.w("Can't promote before initial step !");
            return;
        }
        Edge edge;
        edge = problem.plan.addEdge(threat, source);
        edge.label = 0;
        // TODO consistency check
        if (!new CycleDetector<Action, Edge>(problem.plan).detectCycles()) {
            solve_(problem);
        }
        // Revert !
        problem.plan.removeEdge(edge);
    }

}
