/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.lollipop;

import java.util.Set;
import me.grea.antoine.lollipop.algorithm.Lollipop;
import me.grea.antoine.lollipop.algorithm.PartialOrderPlanning;
import me.grea.antoine.lollipop.benchmark.SolutionChecker;
import me.grea.antoine.lollipop.exception.Failure;
import me.grea.antoine.lollipop.type.Action;
import me.grea.antoine.lollipop.type.Domain;
import me.grea.antoine.lollipop.type.Plan;
import me.grea.antoine.lollipop.type.Problem;
import static me.grea.antoine.utils.Collections.*;
import me.grea.antoine.utils.Log;

/**
 *
 * @author antoine
 */
public class Main {

    static {
        Log.level = Log.Level.VERBOSE;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Failure, InterruptedException {
        // TODO code application logic here
        Action initial = new Action(null, set(1, 2));
        Action goal = new Action(set(3, 4, -5, 6), null);
        Domain domain = new Domain(set(
                new Action(set(1), set(3, 5)),
                new Action(set(7), set(2)),
                new Action(set(4), set()),
                new Action(set(4), set(4)),
                new Action(set(4), set(-5)),
                new Action(set(5), set(6)),
                new Action(set(9), set(3)),
                new Action(set(2), set(4)),
                new Action(set(-7, 8), set(-8, 7)),
                new Action(set(7, -8), set(8, -7))
        ));

        Problem problem = new Problem(initial, goal, domain, new Plan());
        Log.i(problem);
//        Lollipop.solve(problem);
//        Log.out.println("Solution {\n\t" + problem.planToString() + "}");

//        do {
            problem = new Problem(initial, goal, domain, new Plan());
            PartialOrderPlanning.solve(problem);
            Log.out.println("Solution {\n\t" + problem.planToString() + "}");

            Log.i("Solution validity :" + SolutionChecker.check(problem));
//        } while (SolutionChecker.check(problem));

//        problem.initial.effects.add(6);
//        problem.plan.addVertex(problem.initial);
//        Lollipop.solve(problem);
//        Log.out.println("Solution {\n\t" + problem.planToString() + "}");
//        
//        problem.goal.preconditions.remove(3);
//        problem.plan.addVertex(problem.goal);
//        Lollipop.solve(problem);
//        Log.out.println("Solution {\n\t" + problem.planToString() + "}");
    }

}
