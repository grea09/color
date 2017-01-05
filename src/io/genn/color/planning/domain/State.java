/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.genn.color.planning.domain;

import static java.lang.Math.max;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import me.grea.antoine.utils.collection.Collections;

/**
 *
 * @author antoine
 * @param <F>
 */
public class State<F extends Fluent> extends HashSet<F> implements Set<F> {

    public final boolean closed;
    public final boolean unmodifiable;

    public State() {
        this(false, true);
    }

    public State(boolean closed) {
        this(closed, true);
    }

    public State(boolean closed, boolean unmodifiable) {
        this.closed = closed;
        this.unmodifiable = unmodifiable;
    }

    public State(F... array) {
        this(Arrays.asList(array), false, true);
    }
    
    public State(Collection<? extends F> c) {
        this(c, false, true);
    }

    public State(Collection<? extends F> c, boolean closed) {
        this(c, closed, true);
    }
    
    public State(Collection<? extends F> c, boolean closed, boolean unmodifiable) {
        super(c);
        for (F f : c) {
            if (contradicts(f)) {
                remove(f);
            }
        }

        this.closed = closed;
        this.unmodifiable = unmodifiable;
    }

    public State(State other) {
        super(other);
        this.closed = other.closed;
        this.unmodifiable = other.unmodifiable;
    }

    @Override
    public boolean add(F e) {
        if (unmodifiable || contradicts(e)) {
            return false;
        }
        return super.add(e);
    }

    @Override
    public boolean contains(Object o) {
        if (closed && ((F) o).negative() && !contradicts((F) o)) {
            return true;
        }
        return super.contains(o);
    }

    @Override
    public void clear() {
        if (!unmodifiable) {
            super.clear(); //To change body of generated methods, choose Tools | Templates.
        }
    }

    @Override
    public boolean remove(Object o) {
        if (unmodifiable) {
            return false;
        }
        return super.remove(o); //To change body of generated methods, choose Tools | Templates.
    }

    public boolean contradicts(F fluent) {
        if(super.contains(fluent))
            return false;
        if(closed && !fluent.negative())
            return true;
        for (F counter : this) { //FIXME : optimize this when further changes are made
            if (counter.contradicts(fluent)) {
                return true;
            }
        }
        return false;
    }

    public boolean unifies(F fluent) {
        if(contains(fluent))
            return true;
        for (F agree : this) { //FIXME : optimize this when further changes are made
            if (agree.unifies(fluent)) {
                return true;
            }
            if (agree.contradicts(fluent)) {
                return false;
            }
        }
        if(closed && fluent.negative())
            return true;
        return false;
    }

    @Override
    public String toString() {
        String accumulator = "";
        accumulator = (stream().map((fluent) -> fluent + ",").reduce(accumulator, String::concat));
        return "{" + accumulator.substring(0,max(0,accumulator.length()-1)) + '}';
    }
    
    

}
