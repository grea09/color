/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.soda.test;

import java.util.Deque;
import java.util.HashSet;
import java.util.Set;
import me.grea.antoine.log.Log;
import me.grea.antoine.soda.exception.Failure;
import me.grea.antoine.soda.exception.Success;
import me.grea.antoine.soda.type.Action;
import me.grea.antoine.soda.type.Flaw;
import me.grea.antoine.soda.type.Plan;
import me.grea.antoine.soda.type.Problem;
import me.grea.antoine.soda.type.Resolver;
import me.grea.antoine.soda.type.SubGoal;
import me.grea.antoine.soda.type.Threat;
import org.jgrapht.alg.CycleDetector;
import static me.grea.antoine.soda.utils.Collections.*;

/**
 *
 * @author antoine
 */
public class ValidProblemGenerator {

    public static Set<Problem> generate(int enthropy, int number) {
        Set<Problem> problems = new HashSet<>(number);
        while (problems.size() < number) {
            Problem problem = new Problem(
                    new Action(null, Dice.rollNonZero(-enthropy, enthropy, enthropy / 2)),
                    new Action(Dice.rollNonZero(-enthropy, enthropy, enthropy / 2), null),
                    new HashSet<>(),
                    new Plan()
            );
            
            for (int i = 0; i < enthropy / 2; i++) {
                problem.actions.add(ValidActionGenerator.generate(enthropy));
            }
            Set<Integer> provided = problem.initial.effects;
            
            while(!provided.containsAll(problem.goal.preconditions))
            {
                Action action = ValidActionGenerator.generateWith(provided, Dice.pick(problem.goal.preconditions));
                problem.actions.add(action);
                provided.addAll(action.effects);
            }
            problem.plan = new Plan();
            problem.plan.addVertex(problem.initial);
            problem.plan.addVertex(problem.goal);
            problems.add(problem);
        }
        return problems;
    }

}
