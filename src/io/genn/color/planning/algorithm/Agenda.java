/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.genn.color.planning.algorithm;

import java.util.ArrayList;
import java.util.Set;
import io.genn.color.planning.domain.fluents.Fluent;
import io.genn.color.planning.domain.Problem;
import me.grea.antoine.utils.collection.Collections;
import me.grea.antoine.utils.text.Formater;

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

	@Override
	public String toString() {
		return Formater.toString(this, "\n");
	}

	@Override
	public boolean add(Flaw e) {
		if(contains(e))
			return false;
		return super.add(e); //To change body of generated methods, choose Tools | Templates.
	}
	
	
	
	
    
}
