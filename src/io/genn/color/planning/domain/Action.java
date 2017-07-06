/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.genn.color.planning.domain;

import io.genn.world.Flow;
import io.genn.world.data.Entity;
import io.genn.world.data.Store;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author antoine
 */
public class Action<F extends Fluent> {

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
	public final Entity image;
	public final List<Entity> parameters;
	public final State<F> pre;
	public final State<F> eff;
	public final Flag flag;
	private final Flow flow;
	private final Store s;

	public Action(State<F> pre, State<F> eff, Flag flag, Entity image, Flow flow) {
		this.pre = new State(pre);
		this.eff = new State(eff);
		this.flag = flag;
		this.image = image;
		this.flow = flow;
		this.s = flow.store;
		this.name = s.name(image);
		this.parameters = s.signature(image);
	}
	
	public Action(Entity image, Entity pre, Entity eff, Flow flow, Class<? extends F> clas) {
		this.image = image;
		this.flow = flow;
		this.s = flow.store;
		this.name = s.name(image);
		this.parameters = s.signature(image);
		this.flag = name.equals("init") ? Flag.INIT : (name.equals("goal") ? Flag.GOAL : Flag.NORMAL);
		this.pre = new State(pre, flow, clas);
		this.eff = new State(eff, flow, clas);
	}

	public Action(Action other) {
		this(other.pre, other.eff, other.flag, other.image, other.flow);
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
		final Action<?> other = (Action<?>) obj;
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
