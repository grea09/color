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
import me.grea.antoine.log.Log;
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
            solve_(problem);
            throw new Failure();
        } catch (Success s) {
            //TODO add plan building
            s.printStackTrace(Log.out);
        }
    }

    private static void solve_(Problem problem) throws Success {

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
            return; //Failure
        }
        throw new Success(problem);
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
        unthreaten(problem, candidate);
        Log.d("Action " + candidate + " not suited, reverting");
        problem.plan.removeEdge(candidate, toSatisfy);
        if (problem.plan.outDegreeOf(candidate) == 0) {
            problem.plan.removeVertex(candidate);
        }
    }

    private static void unthreaten(Problem problem, Action candidate) throws Success { //TODO ignore initial and goal
        for (int effect : candidate.effects) {
//            if (effect < 0) // BEWARE : Effect 0 has no effect
//            {
            for (Edge oposite : problem.plan.edgeSet()) {
                if ((Integer) oposite.label == -effect) { //Uh oh ?
                    Action source = problem.plan.getEdgeSource(oposite);
                    Action target = problem.plan.getEdgeTarget(oposite);
                    if (problem.plan.getEdge(candidate, source) == null
                            && problem.plan.getEdge(target, candidate) == null) {
                        Log.w("" + candidate + " is a threat to the link " + source + " => " + target);
                        demote(problem, source, target, candidate, effect);
                        promote(problem, source, target, candidate, effect);
                        return; //failure
                    }
                }
            }
//            }
        }
        solve_(problem);
    }

    private static void demote(Problem problem, Action source, Action target, Action threat, int effect) throws Success {
        Log.d("Demoting " + threat);
        Edge edge;
        if (effect < 0) {
            edge = problem.plan.addEdge(threat, source);
        } else  {
            edge = problem.plan.addEdge(source, threat);
        }
        edge.label = 0;
        // TODO consistency check
        solve_(problem);
        // Revert !
        problem.plan.removeEdge(edge);
    }

    private static void promote(Problem problem, Action source, Action target, Action threat, int effect) throws Success {
        Log.d("Promoting " + threat);
        Edge edge;
        if (effect < 0) {
            edge = problem.plan.addEdge(threat, target);
        } else  {
            edge = problem.plan.addEdge(target, threat);
        }
        edge.label = 0;
        // TODO consistency check
        solve_(problem);
        // Revert !
        problem.plan.removeEdge(edge);
    }

}
