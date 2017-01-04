/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.genn.color.planning.algorithm;

import io.genn.color.planning.domain.Fluent;
import io.genn.color.planning.flaw.Agenda;
import io.genn.color.planning.flaw.Flaw;
import io.genn.color.planning.problem.Problem;
import me.grea.antoine.utils.Log;

/**
 *
 * @author antoine
 */
public abstract class Planner<F extends Fluent> {
    
    protected final Problem<F> problem;
    protected Agenda<F> agenda;

    public Planner(Problem problem) {
        this.problem = problem;
    }

    public boolean solve() {
        Log.v(problem);
        if (problem.goal.pre.isEmpty()) {
            problem.plan.addEdge(problem.initial, problem.goal);
            Log.i("Goal is empty !");
            return true;
        }

        try {
            Flaw<F> fail = refine();
            Log.w("Failure : flaw " + fail + " is unsolvable !");
            return false;
        } catch (Success ex) {
            Log.i("Success !");
            return true;
        }
    }
    
    public abstract Flaw<F> refine() throws Success;
    
}
