/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.genn.color.planning.algorithm;

import java.util.Objects;
import io.genn.color.planning.domain.Action;
import io.genn.color.planning.domain.fluents.Fluent;
import io.genn.color.planning.problem.Problem;

/**
 *
 * @author antoine
 * @param <F>
 */
public abstract class Flaw<F extends Fluent<F, ?>> {

	public final F fluent;
	public final Action<F, ?> needer;

	public Flaw(F fluent, Action<F, ?> needer) {
		this.fluent = fluent;
		this.needer = needer;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 41 * hash + Objects.hashCode(this.fluent);
		hash = 41 * hash + Objects.hashCode(this.needer);
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
		final Flaw<?> other = (Flaw<?>) obj;
		if (!Objects.equals(this.fluent, other.fluent)) {
			return false;
		}
		if (!Objects.equals(this.needer, other.needer)) {
			return false;
		}
		return true;
	}

}
