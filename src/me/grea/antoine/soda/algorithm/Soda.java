/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.soda.algorithm;

import me.grea.antoine.log.Log;
import me.grea.antoine.soda.exception.Failure;
import me.grea.antoine.soda.exception.Success;
import me.grea.antoine.soda.type.Plan;
import me.grea.antoine.soda.type.Problem;

/**
 *
 * @author antoine
 */
public class Soda {
    
    public static void solve(Problem problem)
    {
        problem.plan = ProperPlan.properPlan(problem.goal, problem.actions);
        DefectResolver.clean(problem);
        PartialOrderPlanning pop = new PartialOrderPlanning(problem);
        while (true) {
            try {
                pop.solve();
            } catch (Success ex) {
                Log.i(ex);
                DefectResolver.clean(problem);
                break;
            } catch (Failure ex) {
                Log.e(ex);
            }
            SoftSolving.heal(problem);
        }
    }
    
}
