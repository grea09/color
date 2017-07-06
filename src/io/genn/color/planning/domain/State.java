/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.genn.color.planning.domain;

import io.genn.world.Flow;
import io.genn.world.data.Entity;
import io.genn.world.data.Store;
import static io.genn.world.data.Types.STATEMENT;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import me.grea.antoine.utils.log.Log;
import me.grea.antoine.utils.text.Formater;

/**
 *
 * @author antoine
 * @param <F>
 */
public class State<F extends Fluent> extends HashSet<F> {

	public final boolean closed;

	public State() {
		this(false);
	}

	public State(boolean closed) {
		this.closed = closed;
	}

	public State(Entity collection, Flow flow, Class<? extends F> clas) {
		Store s = flow.store;
		if (collection != null) {
			Collection<Entity> fluents = s.value(collection);
			for (Entity fluent : fluents) {
				assert (STATEMENT.equals(s.type(fluent)));
				F f = null;
				try {
					f = clas.getConstructor(Entity.class, Flow.class).
							newInstance(
									fluent, flow);
				} catch (NoSuchMethodException | SecurityException
						| InstantiationException | IllegalAccessException
						| IllegalArgumentException | InvocationTargetException ex) {
					Log.f(ex);
				}

				if (!contradicts(f)) {
					add(f);
				}
			}
		}

		this.closed = false;
	}

	public State(F... array) {
		this(Arrays.asList(array), false);
	}

	public State(Collection<? extends F> c) {
		this(c, false);
	}

	public State(Collection<? extends F> c, boolean closed) {
		super(c);
		for (F f : c) {
			if (contradicts(f)) {
				remove(f);
			}
		}

		this.closed = closed;
	}

	public State(State other) {
		super(other);
		this.closed = other.closed;
	}

	@Override
	public boolean add(F e) {
		if (contradicts(e)) {
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

	public boolean contradicts(F fluent) {
		if (super.contains(fluent)) {
			return false;
		}
		if (closed && !fluent.negative()) {
			return true;
		}
		for (F counter : this) { //FIXME : optimize this when further changes are made
			if (counter.contradicts(fluent)) {
				return true;
			}
		}
		return false;
	}

	public boolean unifies(F fluent) {
		if (contains(fluent)) {
			return true;
		}
		for (F agree : this) { //FIXME : optimize this when further changes are made
			if (agree.unifies(fluent)) {
				return true;
			}
			if (agree.contradicts(fluent)) {
				return false;
			}
		}
		if (closed && fluent.negative()) {
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return Formater.toString(this);
	}
}
