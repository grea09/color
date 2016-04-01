/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.lollipop.type;

import java.util.Collection;
import java.util.HashSet;
import me.grea.antoine.lollipop.mechanism.ProperPlan;
import me.grea.antoine.lollipop.mechanism.Ranking;

/**
 *
 * @author antoine
 */
public class Domain extends HashSet<Action> {

    public final ProperPlan properPlan;
    public final Ranking ranking;

    public Domain() {
        
        this.properPlan = new ProperPlan(this);
        this.ranking = new Ranking(this);
    }

    public Domain(Collection<? extends Action> c) {
        super(c);
        this.properPlan = new ProperPlan(this);
        this.ranking = new Ranking(this);
    }

}
