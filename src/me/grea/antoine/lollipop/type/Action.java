/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.lollipop.type;

import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author antoine
 */
public class Action {

    public Set<Integer> preconditions;
    public Set<Integer> effects;
    public final boolean fake;
    public char symbol;

    public Action() {
        this(new HashSet<>(), new HashSet<>());
    }

    public Action(Action action) {
        this(action.preconditions, action.effects);
    }

    public Action(Collection<Integer> preconditions, Collection<Integer> effects) {
        this(preconditions, effects, false);
    }

    public Action(Collection<Integer> preconditions, Collection<Integer> effects, boolean fake) {
        symbol = fake ? '₳' : (effects == null ? 'G' : (preconditions == null ? 'I' : 'A'));
        if (preconditions == null) {
            this.preconditions = new HashSet();
        } else {
            this.preconditions = new HashSet(preconditions);
        }
        if (effects == null) {
            this.effects = new HashSet();
        } else {
            this.effects = new HashSet(effects);
        }
        this.fake = fake;
    }
    
    @Override
    public String toString() {
        return symbol + (symbol == 'G' || symbol == 'I' ? "" : ("("
                + (preconditions.isEmpty() ? "" : "⊧" + preconditions)
                + (effects.isEmpty() ? "" : "+" + effects) + ")"));
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + Objects.hashCode(this.preconditions);
        hash = 37 * hash + Objects.hashCode(this.effects);
        hash = 37 * hash + (this.fake ? 1 : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Action other = (Action) obj;
        if (this.fake != other.fake) {
            return false;
        }
        if (!Objects.equals(this.preconditions, other.preconditions)) {
            return false;
        }
        if (!Objects.equals(this.effects, other.effects)) {
            return false;
        }
        return true;
    }

    public boolean isSpecial() {
        return !fake && (symbol != 'A');
    }
    
    
}
