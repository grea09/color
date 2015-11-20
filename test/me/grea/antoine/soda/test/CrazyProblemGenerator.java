/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.soda.test;

import java.util.HashSet;
import java.util.Set;
import me.grea.antoine.soda.type.Action;
import me.grea.antoine.soda.type.Edge;
import me.grea.antoine.soda.type.Plan;
import me.grea.antoine.soda.type.Problem;

/**
 *
 * @author antoine
 */
public class CrazyProblemGenerator {
    
    public static Set<Problem> generate(int enthropy, int number) {
        Set<Problem> problems = new HashSet<>(number);
        while(problems.size() < number)
        {
            Problem problem = new Problem(
                    new Action(null, Dice.roll(-enthropy, enthropy, enthropy /2)), 
                    new Action(Dice.roll(-enthropy, enthropy, enthropy /2), null), 
                    CrazyActionGenerator.generate(enthropy, Dice.roll(enthropy)), 
                    new Plan()
            );
            for (int i = 0; i < problem.actions.size()/2; i++) {
                Action source = Dice.pick(problem.actions);
                Action target = Dice.pick(problem.actions);
                problem.plan.addVertex(source);
                problem.plan.addVertex(target);
                Edge edge = problem.plan.addEdge(source, target);
                if(edge == null)
                {
                    problem.plan.getEdge(source, target).labels.addAll(source.effects);
                }
                else
                {
                    edge.labels = source.effects;
                }
            }
            problems.add(problem);
        }
        return problems;
    }
    
}
