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
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.grea.antoine.log.Log;
import org.jgrapht.alg.CycleDetector;
import psptest.type.Action;
import psptest.type.Edge;
import psptest.exception.Failure;
import psptest.type.Problem;
import psptest.exception.Success;
import psptest.type.Problem.SubGoal;
import psptest.type.Problem.Threat;

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
            Log.v(s);
        }
    }

    private static void solve_(Problem problem) throws Success {
        try {
            unthreaten(problem);
            satisfy(problem);
            throw new Success();
        } catch (Failure ex) {
            Log.e(ex);
        }
    }

    private static void satisfy(Problem problem) throws Success, Failure {
//        Set<SubGoal> subgoals = problem.suboals();
//        Log.d("Subgoals : " + subgoals);
        SubGoal subGoal = problem.findSubGoal();
        if (subGoal != null) {
            if (problem.initial.effects.contains(subGoal.subgoal)) {
                insert(problem, subGoal, problem.initial);
            }

            for (Action step : problem.plan.vertexSet()) {
                if (step.effects.contains(subGoal.subgoal)) {
                    insert(problem, subGoal, step);
                }
            }
            for (Action action : problem.actions) {
                if (action.effects.contains(subGoal.subgoal)) {
                    insert(problem, subGoal, action);
                }
            }
            throw new Failure(subGoal);
        }
        //Success as we return
    }

    private static void insert(Problem problem, SubGoal subGoal, Action candidate) throws Success {

        //TODO Fail if negative effects
        subGoal.satisfy(candidate);

        solve_(problem);

        subGoal.revert();
    }

    private static void unthreaten(Problem problem) throws Success, Failure { //TODO ignore initial and goal
        Threat threat = problem.findThreat();
        if (threat != null) {
            Log.w("Found a threat " + threat);
            demote(problem, threat);
            promote(problem, threat);
            throw new Failure(threat);
        }
    }

    private static void demote(Problem problem, Threat threat) throws Success {
        try {
            threat.demote();
        } catch (Failure ex) {
            return;
        }

        if (problem.consistent()) {
            solve_(problem);
        }

        threat.undemote();
    }

    private static void promote(Problem problem, Threat threat) throws Success {
        try {
            threat.promote();
        } catch (Failure ex) {
            return;
        }

        if (problem.consistent()) {
            solve_(problem);
        }

        threat.unpromote();
    }

}
