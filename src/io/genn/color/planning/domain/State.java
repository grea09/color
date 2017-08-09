/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.genn.color.planning.domain;

import java.lang.reflect.InvocationTargetException;
import java.security.spec.ECField;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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

	public <E> Map<E, E> unify(F fluent) {
		if (contains(fluent)) {
			return new HashMap<>();
		}
		for (F agree : this) { //FIXME : optimize this when further changes are made
			Map<E, E> unify = agree.unify(fluent);
			if (agree.contradicts(fluent)) {
				return null;
			}
			if (unify != null) {
				return unify;
			}
		}
		if (closed && fluent.negative()) {
			return new HashMap<>();
		}
		return null;
	}

	public <E> State<F> instanciate(Map<E, E> unify) {
		State<F> instance = new State<>();
		for (F toInstanciate : this) {
			F instanciated = (F) toInstanciate.instanciate(unify);
			if (instanciated == null) {
				instance.add(toInstanciate);
				continue;
			}
			instance.add(instanciated);
		}
		if (instance.equals(this)) {
			return this;
		}
		return instance;
	}

	@Override
	public String toString() {
		return Formater.toString(this);
	}

	boolean coherent() {
		for (F fluent : this) {
			if (!fluent.coherent()) {
				return false;
			}
			if (contradicts(fluent)) {
				return false;
			}
		}
		return true;
	}
}
