/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.lollipop;

import me.grea.antoine.lollipop.type.Action;
import java.util.Set;
import me.grea.antoine.lollipop.algorithm.PartialOrderOptimizedPlanning;
import me.grea.antoine.lollipop.algorithm.PartialOrderPlanning;
import me.grea.antoine.lollipop.algorithm.ProperPlan;
import me.grea.antoine.lollipop.exception.Failure;
import me.grea.antoine.utils.Log;
import me.grea.antoine.lollipop.type.Plan;
import me.grea.antoine.lollipop.type.Problem;
import static me.grea.antoine.utils.Collections.*;

/**
 *
 * @author antoine
 */
public class Test {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Failure {
        // TODO code application logic here
        Action initial = new Action(null, set(-36, -39, 40, 42, 43, -45, 13, 46, -15, -16, -17, 19, -24, -29, 30));
        Action goal = new Action(set(-2, -4, 4, 6, 8, 9, 10, 13, 19, 20, -23, -24, -25, -26, -27, 34, 38, -39, -40, 40, -43, -44, 43, 45, 50), null);
        Set<Action> actions = set(
                new Action(set(34, -30), set(-48)),
                new Action(set(), set()),
                new Action(set(-37), set(-39, -43)),
                new Action(set(-4), set()),
                new Action(set(), set()),
                new Action(set(-44, -30), set(42, 31)),
                new Action(set(-1, -50), set(2)),
                new Action(set(-11, 43), set(12)),
                new Action(set(40), set()),
                new Action(set(-38, -40), set(18)),
                new Action(set(-34, -31), set(-35, -46)),
                new Action(set(34, -30), set()),
                new Action(set(-35, 24), set(37, 21)),
                new Action(set(), set(18, 35)),
                new Action(set(14), set(-24)),
                new Action(set(), set()),
                new Action(set(-49, 34), set(10, -11)),
                new Action(set(), set()),
                new Action(set(49), set(3, -41)),
                new Action(set(), set(41, -47)),
                new Action(set(-46), set(38)),
                new Action(set(-23), set()),
                new Action(set(-22, 10), set(19, 23)),
                new Action(set(-17), set(24, 11)),
                new Action(set(20), set()),
                new Action(set(), set()),
                new Action(set(25), set(-36)),
                new Action(set(), set()),
                new Action(set(4), set()),
                new Action(set(35, 40), set(-25)),
                new Action(set(44), set(32, 49)),
                new Action(set(), set()),
                new Action(set(), set(1, -10)),
                new Action(set(), set()),
                new Action(set(), set(27)),
                new Action(set(), set()),
                new Action(set(7), set(6)),
                new Action(set(-22, -10), set())
        );

        Problem problem = new Problem(initial, goal, actions, ProperPlan.properPlan(goal, actions));

        System.out.println(problem);

        PartialOrderOptimizedPlanning.solve(problem);

        Log.out.println("Solution {\n\t" + problem.planToString() + "}");
        Log.out.println("Violation : " + problem.plan.violation());
    }

}
