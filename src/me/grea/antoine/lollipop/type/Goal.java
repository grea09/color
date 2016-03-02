/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.lollipop.type;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author antoine
 */
public class Goal {

    public Set<Integer> preconditions;

    public Goal() {
        this(new HashSet<>());
    }

    public Goal(Collection<Integer> preconditions) {
        if (preconditions == null) {
            this.preconditions = new HashSet();
        } else {
            this.preconditions = new HashSet(preconditions);
        }
    }

    @Override
    public String toString() {
        return "G[|=" + preconditions + ']';
    }

}
