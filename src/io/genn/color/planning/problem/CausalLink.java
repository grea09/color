/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.genn.color.planning.problem;

import java.util.Objects;
import io.genn.color.planning.domain.Action;
import io.genn.color.planning.domain.fluents.Fluent;
import io.genn.color.planning.domain.State;
import java.util.Collections;
import me.grea.antoine.utils.graph.Edge;

/**
 *
 * @author antoine
 */
public class CausalLink implements Edge<Action> {

	private final Action source;
	private final Action target;
	public final State causes;

	public CausalLink(Action source, Action target) {
		this(source, target, new State<>());
	}

	public CausalLink(Action source, Action target, State causes) {
		this.source = source;
		this.target = target;
		this.causes = new State<>(Collections.unmodifiableSet(causes), false);
	}

	public CausalLink(CausalLink other) {
		this(other.source, other.target, other.causes);
	}

	public CausalLink(CausalLink other, State newCauses) {
		this(other.source, other.target, newCauses);
	}

	@Override
	public Action source() {
		return source;
	}

	@Override
	public Action target() {
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
		final CausalLink other = (CausalLink) obj;
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
		return source + ((causes != null && !causes.isEmpty()) ?
						 " =[" + causes + "]" :
						 "") + "=> " + target;
	}

	public <F extends Fluent<F, ?>> CausalLink remove(F fluent) {
		State<F> state = new State<>(causes, false);
		state.remove(fluent);
		return new CausalLink(this, state);
	}

	public <F extends Fluent<F, ?>> CausalLink add(F fluent) {
		State<F> state = new State<>(causes, false);
		state.add(fluent);
		return new CausalLink(this, state);
	}

}
