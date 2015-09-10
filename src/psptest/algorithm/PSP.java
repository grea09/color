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

    private static void insert(Problem problem, int subgoal, Action toSatisfy, Action candidate) throws Success {
        Log.d("Inserting " + candidate + " to satisfy precondition " + subgoal + " of goal " + toSatisfy);
        problem.plan.addVertex(candidate);
        problem.plan.addEdge(candidate, toSatisfy);
        solve_(problem);
        Log.d("Action " + candidate + " not suited, reverting");
        problem.plan.removeEdge(candidate, toSatisfy);
        if (problem.plan.outDegreeOf(candidate) == 0) {
            problem.plan.removeVertex(candidate);
        }
    }

    private static void prepone(Problem problem, Map.Entry<Integer, Action> threat) throws Success {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private static Map<Integer, Action> threats(Problem problem) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private static void solve_(Problem problem) throws Success {
//        Map<Integer, Action> threats = threats(initial, goal, plan);
//        for (Map.Entry<Integer, Action> threat : threats.entrySet()) { //random select …
//            //CHOICE
//            prepone(initial, goal, plan, threat);
//            postpone(initial, goal, plan, threat);
//            return; //Failure
//        }
        
        
        
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

    private static void postpone(Problem problem, Map.Entry<Integer, Action> threat) throws Success {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public static void solve(Problem problem) throws Failure {
        try {
            solve_(problem);
            throw new Failure();
        } catch (Success s) {
            //TODO add plan building
        }
    }


    private static Map<Integer, Action> subgoal(Problem problem) {
        Map<Integer, Action> subgoals = new HashMap<>();
        Deque<Action> open = new ArrayDeque<>(Arrays.asList(problem.goal));
        while (!open.isEmpty()) {
            Action current = open.pollLast();
            for (int effect : current.effects) {
                subgoals.remove(effect);
            }
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
    
}
