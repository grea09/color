/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.liris.lollipop2.planning.flaw;

import java.util.ArrayList;
import java.util.Set;
import me.grea.antoine.liris.lollipop2.planning.domain.Fluent;
import me.grea.antoine.liris.lollipop2.planning.problem.Problem;

/**
 *
 * @author antoine
 */
public abstract class Agenda<F extends Fluent> extends ArrayList<Flaw<F>> {
    
    
    protected final Problem problem;
    
    public Agenda(Agenda other)
    {
        super(other);
        problem = other.problem;
    }
    
    public Agenda(Problem problem)
    {
        this.problem = problem;
        populate();
    }
    
    protected abstract void populate();
    public abstract Flaw<F> choose();
    public abstract void related(Resolver<F> resolver);
    
}
