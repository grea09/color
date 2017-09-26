/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.genn.color.planning.algorithm;

import io.genn.color.planning.domain.fluents.Fluent;
import io.genn.color.planning.domain.Problem;
import me.grea.antoine.utils.log.Log;

/**
 *
 * @author antoine
 */
public abstract class Planner {
    
    protected final Problem problem;
    protected Agenda agenda;

    public Planner(Problem problem) {
        this.problem = problem;
    }

    public boolean solve() {
        Log.i(problem);
        if (problem.goal.pre.isEmpty()) {
            problem.plan.addEdge(problem.initial, problem.goal);
            Log.i("Goal is empty !");
            return true;
        }

        try {
            Flaw fail = refine();
            Log.e("Failure : flaw " + fail + " is unsolvable !");
            return false;
        } catch (Success ex) {
            Log.i("Success !");
            return true;
        }
    }
    
    public abstract Flaw refine() throws Success;
    
}
