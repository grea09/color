/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.genn.color.planning.problem;

import java.util.Objects;
import io.genn.color.graph.Edge;
import io.genn.color.planning.domain.Action;
import io.genn.color.planning.domain.Fluent;
import io.genn.color.planning.domain.IntFluent;
import io.genn.color.planning.domain.State;

/**
 *
 * @author antoine
 */
public class CausalLink<F extends Fluent> implements Edge<Action<F>> {

    private final Action<F> source;
    private final Action<F> target;
    public final State<F> causes;

    public CausalLink(Action<F> source, Action<F> target) {
        this(source, target, new State<>());
    }

    public CausalLink(Action<F> source, Action<F> target, State<F> causes) {
        this.source = source;
        this.target = target;
        this.causes = new State<>(causes, false, true);
    }

    public CausalLink(CausalLink<F> other) {
        this(other.source, other.target, other.causes);
    }

    public CausalLink(CausalLink<F> other, State<F> newCauses) {
        this(other.source, other.target, newCauses);
    }

    @Override
    public Action<F> source() {
        return source;
    }

    @Override
    public Action<F> target() {
        return target;
    }

    @Override
    public int weight() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.source);
        hash = 79 * hash + Objects.hashCode(this.target);
        hash = 79 * hash + Objects.hashCode(this.causes);
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
        final CausalLink<?> other = (CausalLink<?>) obj;
        if (!Objects.equals(this.source, other.source)) {
            return false;
        }
        if (!Objects.equals(this.target, other.target)) {
            return false;
        }
        if (!Objects.equals(this.causes, other.causes)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "<source " + source + ", causes " + causes + ", target " + target + '>';
    }

    public CausalLink<F> remove(F fluent) {
        State<F> state = new State<>(causes, false, false);
        state.remove(fluent);
        return new CausalLink<>(this, state);
    }
    
    public CausalLink<F> add(F fluent) {
        State<F> state = new State<>(causes, false, false);
        state.add(fluent);
        return new CausalLink<>(this, state);
    }

}
