/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.genn.color.planning.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author antoine
 */
public class IntFluent implements Fluent<IntFluent, Integer> {

	public final int value;

	public IntFluent(int value) {
		this.value = value;
	}

	public IntFluent(IntFluent other) {
		this(other.value);
	}

	@Override
	public boolean unifies(IntFluent lesser) {
		return this.value == lesser.value;
	}

	@Override
	public boolean contradicts(IntFluent counter) {
		return this.value == -counter.value;
	}

	@Override
	public int hashCode() {
		return value;
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
		final IntFluent other = (IntFluent) obj;
		if (this.value != other.value) {
			return false;
		}
		return true;
	}

	@Override
	public IntFluent negate() {
		return new IntFluent(-value);
	}

	@Override
	public boolean negative() {
		return value < 0;
	}

	@Override
	public String toString() {
		return "" + value;
	}

	@Override
	public Map<Integer, Integer> unify(IntFluent lesser) {
		return unifies(lesser) ? new HashMap<>() : null;
	}

	@Override
	public FluentControl<IntFluent, Integer> control() {
		return (Action<IntFluent, Integer> lifted, List<Integer> parameters) ->
				 lifted.image;
	}

	@Override
	public IntFluent instanciate(Map<Integer, Integer> unify) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public boolean coherent() {
		return value != 0;
	}

}
