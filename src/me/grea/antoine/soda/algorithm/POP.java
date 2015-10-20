/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.soda.algorithm;

import java.util.HashSet;
import java.util.Set;
import me.grea.antoine.log.Log;
import me.grea.antoine.soda.type.Action;
import me.grea.antoine.soda.exception.Failure;
import me.grea.antoine.soda.type.Problem;
import me.grea.antoine.soda.exception.Success;
import me.grea.antoine.soda.type.Problem.SubGoal;
import me.grea.antoine.soda.type.Problem.Threat;
import me.grea.antoine.soda.type.Problem.Loop;

/**
 *
 * @author antoine
 */
public class POP {

    public static void solve(Problem problem) {
        try {
            POPMinus.clean(problem);
            solve_(problem);
            Log.e("Problem unsolvable ! Now trying softer");
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
//        if(problem.partialSolutions.containsKey(subGoal))
//        {
//            Log.w("Loop detected with flaw " + subGoal);
//            throw new Failure(problem, subGoal);
//        }

        if (subGoal != null) {
            try {
                if (problem.initial.effects.contains(subGoal.fluent)) {
                    insert(problem, subGoal, problem.initial);
                }

                Set<Action> steps = new HashSet<>(problem.plan.vertexSet());
                steps.remove(problem.initial);
                steps.removeAll(problem.quarantine);
                for (Action step : steps) {
                    if (step.effects.contains(subGoal.fluent)) {
                        insert(problem, subGoal, step);
                    }
                }
                
                Set<Action> actions = new HashSet<>(problem.actions);
                actions.remove(problem.initial);
                actions.removeAll(steps);
                actions.removeAll(problem.quarantine);
                for (Action action : actions) {
                    if ( action.effects.contains(subGoal.fluent)) {
                        insert(problem, subGoal, action);
                    }
                }
//                for (Action contamined : problem.quarantine) {
//                    if (contamined.effects.contains(subGoal.fluent)) {
//                        Log.w("Forced to use contamined action " + contamined);
//                        insert(problem, subGoal, contamined);
//                    }
//                }
            } catch (Failure failure) {
                if (failure.cause instanceof Loop) {
                    Loop loop = (Loop) failure.cause;
                    problem.quarantine.add(problem.plan.getEdgeSource(loop.looping));
                    problem.quarantine.add(problem.plan.getEdgeTarget(loop.looping));
                    Log.d("Quarantine is now :" + problem.quarantine);
                }
                throw failure;
            }
            throw new Failure(problem, subGoal);
        }
        //Success as we return
    }

    private static void insert(Problem problem, SubGoal subGoal, Action candidate) throws Success, Failure {
        subGoal.satisfy(candidate);

        if (problem.consistent()) {
            solve_(problem);
        }

        subGoal.revert();
    }

    private static void unthreaten(Problem problem) throws Success, Failure {
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
