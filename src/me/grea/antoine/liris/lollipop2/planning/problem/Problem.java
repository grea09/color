/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.liris.lollipop2.planning.problem;

import me.grea.antoine.liris.lollipop2.planning.algorithm.pop.PopAgenda;
import me.grea.antoine.liris.lollipop2.planning.domain.Action;
import me.grea.antoine.liris.lollipop2.planning.domain.Domain;
import me.grea.antoine.liris.lollipop2.planning.domain.Fluent;
import me.grea.antoine.liris.lollipop2.planning.flaw.Agenda;

/**
 *
 * @author antoine
 */
public class Problem<F extends Fluent> {
    
    public Action<F> initial;
    public Action<F> goal;
    public Domain<F> domain; // not including those above
    public Plan<F> plan;

    public Problem(Action<F> initial, Action<F> goal, Domain<F> domain, Plan<F> plan) {
        this.initial = initial;
        this.goal = goal;
        this.domain = domain;
        this.plan = plan;
        this.plan.addEdge(initial, goal);
    }
    
    public Problem(Action<F> initial, Action<F> goal, Domain<F> domain) {
        this(initial, goal, domain, new Plan<>());
    }
    
    public boolean solved()
    {
        Agenda<F> agenda = new PopAgenda(this);
        return agenda.isEmpty();
    }

    @Override
    public String toString() {
        return "problem :: Problem\nproblem : " + "<initial  " + initial + ", goal " + goal + "\n\tdomain " + domain + "\n\tplan " + plan + "\n>";
    }
    
}
