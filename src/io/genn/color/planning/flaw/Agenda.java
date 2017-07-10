/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.genn.color.planning.flaw;

import java.util.ArrayList;
import java.util.Set;
import io.genn.color.planning.domain.Fluent;
import io.genn.color.planning.problem.Problem;

/**
 *
 * @author antoine
 */
public abstract class Agenda extends ArrayList<Flaw> {
    
    
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
    public abstract Flaw choose();
    public abstract void related(Resolver resolver);
    
}
