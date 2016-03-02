/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.lollipop.agenda;

import java.util.ArrayList;
import me.grea.antoine.lollipop.type.Problem;
import me.grea.antoine.lollipop.type.flaw.Flaw;

/**
 *
 * @author antoine
 */
public abstract class Agenda extends ArrayList<Flaw> {

    protected final Problem problem;
    
    public Agenda(Problem problem)
    {
        this.problem = problem;
        populate();
    }
    
    protected abstract void populate();
    public abstract Flaw choose();
}
