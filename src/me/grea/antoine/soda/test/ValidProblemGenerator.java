/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.soda.test;

import me.grea.antoine.soda.test.*;
import java.util.HashSet;
import java.util.Set;
import me.grea.antoine.log.Log;
import me.grea.antoine.soda.algorithm.PartialOrderPlanning;
import me.grea.antoine.soda.exception.Failure;
import me.grea.antoine.soda.exception.Success;
import me.grea.antoine.soda.type.Action;
import me.grea.antoine.soda.type.Plan;
import me.grea.antoine.soda.type.Problem;
import me.grea.antoine.soda.type.flaw.Threat;

/**
 *
 * @author antoine
 */
public class ValidProblemGenerator {

    public static Set<Problem> generate(int enthropy, int number) {
        Set<Problem> problems = new HashSet<>(number);
        while (problems.size() < number) {
            Action initial = new Action(null, Dice.rollNonZero(-enthropy, enthropy, Dice.roll(1, 3 * (int) Math.round(Math.log(enthropy)))));
            Action goal = new Action(Dice.rollNonZero(-enthropy, enthropy, Dice.roll(3 * (int) Math.round(Math.log(enthropy)), 4 * (int) Math.round(Math.log(enthropy)))), null);
            ValidActionGenerator.clean(initial);
            ValidActionGenerator.clean(goal);

            Problem problem = new Problem(
                    initial,
                    goal,
                    new HashSet<>(),
                    new Plan()
            );

            int tries = enthropy * 2; // I died a little inside
            while (tries-- != 0) {
                for (int i = 0; i < enthropy; i++) {
                    problem.actions.add(ValidActionGenerator.generate(enthropy));
                }
                Set<Integer> current = new HashSet<>(initial.effects);
                for (int precondition : goal.preconditions) {
                    Action action = ValidActionGenerator.generateWith(current, precondition);
                    problem.actions.add(action);
                    current.add(precondition);
                }
                PartialOrderPlanning pop = new PartialOrderPlanning(problem);
                Log.i(problem);
                try {
                    pop.refine();
                } catch (Success ex) {
                    break;
                } catch (Failure ex) {
                    problem.actions.clear();
//                    Set<Threat> threats = Threat.find(problem);
//                    for (Threat threat : threats) {
//                        problem.plan.removeVertex(threat.breaker);
//                    }
//                    DefectResolver.clean(problem);
                    problem.actions.addAll(problem.plan.vertexSet());
                    problem.plan = new Plan();
                    problem.plan.addVertex(problem.initial);
                    problem.plan.addVertex(problem.goal);
                }
            }
            if (problem.plan.vertexSet().size() == 2) {
                continue;
            }

            problem.plan = new Plan();
            problem.plan.addVertex(problem.initial);
            problem.plan.addVertex(problem.goal);
            problems.add(problem);
        }
        return problems;
    }

}
