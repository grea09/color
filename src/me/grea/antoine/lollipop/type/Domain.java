/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.lollipop.type;

import java.util.Collection;
import java.util.HashSet;

/**
 *
 * @author antoine
 */
public class Domain extends HashSet<Action>{
    
    public final Plan operatorGraph;

    public Domain() {
        this.operatorGraph = operatorGraph;
    }

    public Domain(Collection<? extends Action> c) {
        super(c);
        this.operatorGraph = operatorGraph;
    }
    
    
    
}
