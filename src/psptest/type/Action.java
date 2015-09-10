/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package psptest.type;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author antoine
 */
public class Action extends Goal {
    public Set<Integer> effects;

    public Action() {
        this(new HashSet<>(), new HashSet<>());
    }

    public Action(Goal goal) {
        this(goal.preconditions, new HashSet());
    }

    public Action(Action action) {
        this(action.preconditions, action.effects);
    }

    public Action(Collection<Integer> preconditions, Collection<Integer> effects) {
        super(preconditions);
        if (effects == null) {
            this.effects = new HashSet();
        } else {
            this.effects = new HashSet(effects);
        }
    }

    @Override
    public String toString() {
        return "A[|=" + preconditions + " =+" + effects + ']';
    }
    
}
