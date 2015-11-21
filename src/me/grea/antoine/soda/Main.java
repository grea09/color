/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.soda;

import me.grea.antoine.soda.type.Action;
import java.util.Set;
import me.grea.antoine.log.Log;
import me.grea.antoine.soda.algorithm.ProperPlan;
import me.grea.antoine.soda.algorithm.Soda;
import me.grea.antoine.soda.type.Plan;
import me.grea.antoine.soda.type.Problem;
import me.grea.antoine.soda.type.defect.CompetingAction;
import static me.grea.antoine.soda.utils.Collections.*;

/**
 *
 * @author antoine
 */
public class Main {
    

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Action initial = new Action(null, set(1, 2));
        Action goal = new Action(set(-2, 3, 4, -5, 6), null);
        Set<Action> actions = set(new Action(set(1), set(3, 5)),
                new Action(set(2), set(4)),
                new Action(set(4), set(-5)),
                new Action(set(5), set(6)),
                new Action(set(9), set(3)),
                new Action(set(7), set(-2, 2)),
                new Action(set(4), set()),
                new Action(set(4), set(4)),
                new Action(set(-7, 8), set(-8, 7)),
                new Action(set(7, -8), set(8, -7))
        );

        Problem problem = new Problem(initial, goal, actions, new Plan());
        

        System.out.println(problem);
        
        Soda.solve(problem);

        Log.i("Finih !");
        Log.out.println("Solution {\n\t" + problem.planToString() + "}");
        Log.out.println("Violation : " + problem.violation());
    }

}
