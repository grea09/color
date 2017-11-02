/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.genn.color.planning.domain;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import io.genn.color.planning.domain.fluents.Fluent;
import io.genn.color.planning.domain.fluents.FluentControl;
import io.genn.color.planning.plan.Plan;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import static me.grea.antoine.utils.collection.Collections.*;

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
	public final Action instanceOf;
	public final FluentControl<F, E> control;
	public final int level;

	public Action(String name, List<E> parameters, State<F> pre, State<F> eff,
			State<F> constr, Flag flag, E image, Action<F, E> instanceOf,
			FluentControl<F, E> control) {
		this(name, parameters, pre, eff, constr, flag, image, null, instanceOf,
			 control);
	}

	public Action(String name, List<E> parameters, State<F> pre, State<F> eff,
			State<F> constr, Flag flag, E image, Plan method,
			Action<F, E> instanceOf, FluentControl<F, E> control) {
		this.pre = new State(pre);
		this.eff = new State(eff);
		this.constr = new State(constr);
		this.flag = flag;
		this.image = image;
		this.name = name;
		this.parameters = parameters;
		this.method = method;
		this.instanceOf = instanceOf;
		this.control = control;
		int maxLevel = 0;
		if (method != null) {
			for (Action step : method.vertexSet()) {
				if (step.level > maxLevel) {
					maxLevel = step.level;
				}
			}
			maxLevel++;
		}
		this.level = maxLevel;
	}

	public Action(Action<F, E> other) {
		this(other.name, other.parameters, other.pre, other.eff, other.constr,
			 other.flag, other.image, other.instanceOf, other.control);
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

	public Collection<Action<F, E>> instanciate(
			Collection<Map<E, E>> unifications) {
		Collection<Action<F, E>> result = set();
		if (unifications != null) {
			for (Map<E, E> unify : unifications) {
				Action<F, E> instanciate = instanciate(unify);
				if (instanciate != null) {
					result.add(instanciate);
				}
			}
		}
		return result;

	}

	public Action<F, E> instanciate(Map<E, E> unify) {
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
							method, this, control);
	}

	public Collection<Action<F, E>> asked(F asked) {
		return instanciate(eff.provide(asked));
	}

	public Collection<Action<F, E>> given(F given) {
		return instanciate(pre.provide(given));
	}

	public Collection<Action<F, E>> given(State<F> given) {
		Collection<Map<E, E>> result = set();
		for (F f : pre) {
			result.addAll(given.provide(f));
		}
		return instanciate(result);
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
