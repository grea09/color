/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.genn.color.planning.domain;

import io.genn.color.planning.problem.Plan;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import static me.grea.antoine.utils.collection.Collections.first;

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
	public final State<F> constr;
	public final Flag flag;
	public final Plan method;

	public Action(String name, List<E> parameters, State<F> pre, State<F> eff,
			State<F> constr, Flag flag, E image) {
		this(name, parameters, pre, eff, constr, flag, image, null);
	}

	public Action(String name, List<E> parameters, State<F> pre, State<F> eff,
			State<F> constr, Flag flag, E image, Plan method) {
		this.pre = new State(pre);
		this.eff = new State(eff);
		this.constr = new State(constr);
		this.flag = flag;
		this.image = image;
		this.name = name;
		this.parameters = parameters;
		this.method = method;
	}

	public Action(Action<F, E> other) {
		this(other.name, other.parameters, other.pre, other.eff, other.constr,
			 other.flag, other.image);
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

	public List<E> parameterize(Map<E, E> unify) {
		if (parameters == null) {
			return null;
		}
		if (unify == null) {
			return null;
		}
		if (unify.isEmpty()) {
			return parameters;
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
		if (changed) { //TODO here that we do the stuff
			return newParameters;
		}
		return parameters;
	}

	public Action<F, E> instanciate(Map<E, E> unify,
			FluentControl<F, E> control) {
		if (unify == null) {
			return null;
		}
		List<E> newParameters = parameterize(unify);
		if (newParameters == null) {
			return parameters == null ? this : null;
		} else if (newParameters.equals(parameters)) {
			return this;
		}
		State newEff = eff.instanciate(unify);
		State newPre = pre.instanciate(unify);
		State newConstr = constr.instanciate(unify);
		if (!newConstr.coherent()) {
			return null;
		}
		E newImage = control.instanciate(this, newParameters);
		//FIXME parameterize method
		return new Action<>(name, newParameters,
							newPre, newEff, newConstr,
							flag, newImage,
							method);
	}

	public Action<F, E> provide(F toProvide) {
		return instanciate(eff.unify(toProvide), toProvide.control());
	}

	public Action<F, E> provided(F provided) {
		return instanciate(pre.unify(provided), provided.control());
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
