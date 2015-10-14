/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package soda.algorithm;

import me.grea.antoine.log.Log;
import soda.type.Action;
import soda.exception.Failure;
import soda.type.Problem;
import soda.exception.Success;
import soda.type.Problem.SubGoal;
import soda.type.Problem.Threat;

/**
 *
 * @author antoine
 */
public class POP {

    public static void solve(Problem problem) throws Failure{
        try {
            POPMinus.clean(problem);
            solve_(problem);
            Log.w("Problem unsolvable ! Now trying softer");
            POPPlus.soft(problem);
        } catch (Success s) {
            Log.v(s);
            POPMinus.clean(problem);
        }
    }

    private static void solve_(Problem problem) throws Success {
        try {
            unthreaten(problem);
            satisfy(problem);
            throw new Success();
        } catch (Failure ex) {
            Log.w(ex);
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
            throw new Failure(problem, subGoal);
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
            throw new Failure(problem, threat);
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
