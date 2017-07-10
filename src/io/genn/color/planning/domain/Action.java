/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.genn.color.planning.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @author antoine
 */
public class Action<F extends Fluent<F, E>, E> {

	public static enum Flag {
		NORMAL('A'),
		INIT('I') {
			@Override
			public boolean special() {
				return true;
			}
		},
		GOAL('G') {
			@Override
			public boolean special() {
				return true;
			}
		};

		private final Character symbol;

		private Flag(Character symbol) {
			this.symbol = symbol;
		}

		@Override
		public String toString() {
			return symbol.toString();
		}

		public boolean special() {
			return false;
		}
	}

	public final String name;
	public final E image;
	public final List<E> parameters;
	public final State<F> pre;
	public final State<F> eff;
	public final Flag flag;

	public Action(String name, List<E> parameters, State<F> pre, State<F> eff,
			Flag flag, E image) {
		this.pre = new State(pre);
		this.eff = new State(eff);
		this.flag = flag;
		this.image = image;
		this.name = name;
		this.parameters = parameters;
	}

	public Action(Action<F, E> other) {
		this(other.name, other.parameters, other.pre, other.eff, other.flag,
			 other.image);
	}

	@Override
	public String toString() {
		return image.toString();
//				+
//				 (": (" +
//				(pre.isEmpty() ? "" : "pre " + pre) +
//				(!pre.isEmpty() && !eff.isEmpty() ? ", " : "") +
//				(eff.isEmpty() ? "" : "eff " + eff) + ")");
	}

	public Action<F, E> instanciate(F fluent, State<F> in) {
		Map<E, E> unify = in.unify(fluent);
		if (unify == null) {
			return null;
		}
		if (unify.isEmpty()) {
			return this;
		}
		List<E> newParameters = new ArrayList<>();
		boolean changed = false;
		for (E parameter : parameters) {
			if (unify.containsKey(parameter)) {
				newParameters.add(unify.get(parameter));
				changed = true;
			} else {
				newParameters.add(parameter);
			}
		}
		if (changed) {
			return fluent.control().instanciate(this, newParameters);
		}
		return this;
	}

	public Action<F, E> instanciateFor(F toProvide) {
		return instanciate(toProvide, eff);
	}

	public Action<F, E> instanciateFrom(F provided) {
		return instanciate(provided, pre);
	}

	public boolean special() {
		return flag.special();
	}

	public boolean initial() {
		return flag == Flag.INIT;
	}

	public boolean goal() {
		return flag == Flag.GOAL;
	}

	@Override
	public int hashCode() {
//		int hash = 5;
//		hash = 41 * hash + Objects.hashCode(this.pre);
//		hash = 41 * hash + Objects.hashCode(this.eff);
//		hash = 41 * hash + Objects.hashCode(this.flag);
//		return hash;
		return image.hashCode();
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
		final Action<?, ?> other = (Action<?, ?>) obj;
		return image.equals(other.image);
//		if (!Objects.equals(this.pre, other.pre)) {
//			return false;
//		}
//		if (!Objects.equals(this.eff, other.eff)) {
//			return false;
//		}
//		if (this.flag != other.flag) {
//			return false;
//		}
//		return true;
	}

}
