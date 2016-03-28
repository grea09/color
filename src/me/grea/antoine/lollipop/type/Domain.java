/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.lollipop.type;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import me.grea.antoine.lollipop.algorithm.DomainProperPlan;
import me.grea.antoine.lollipop.type.flaw.Cycle;
import org.jgrapht.alg.StrongConnectivityInspector;

/**
 *
 * @author antoine
 */
public class Domain extends HashSet<Action>{

    public static Set<Set<Action>> connectedSets(Plan plan) {
        Set<Set<Action>> connectedSets = new HashSet<>(new StrongConnectivityInspector<>(plan).stronglyConnectedSets());
        return connectedSets;
    }
    
    public final Map<Integer, Set<Action>> providing;
    public final Plan properPlan;
    public final Set<Set<Action>> cycles;
    
    

    public Domain() {
        this.providing = DomainProperPlan.providing(this);
        this.properPlan = DomainProperPlan.build(this);
        this.cycles = connectedSets(properPlan);
    }

    public Domain(Collection<? extends Action> c) {
        super(c);
        this.providing = DomainProperPlan.providing(this);
        this.properPlan = DomainProperPlan.build(this);
        this.cycles = connectedSets(properPlan);
    }
    
    
    
}
