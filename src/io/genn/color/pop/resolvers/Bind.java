/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the change.
 */
package io.genn.color.pop.resolvers;

import io.genn.color.planning.algorithm.Change;
import java.util.Objects;
import java.util.Set;
import io.genn.color.planning.domain.Action;
import io.genn.color.planning.domain.fluents.Fluent;
import io.genn.color.planning.domain.State;
import io.genn.color.planning.algorithm.Resolver;
import io.genn.color.planning.problem.CausalLink;
import io.genn.color.planning.problem.Plan;
import io.genn.color.planning.problem.Solution;
import java.util.Collection;
import static me.grea.antoine.utils.collection.Collections.list;

/**
 *
 * @author antoine
 */
public class Bind<F extends Fluent<F, ?>> implements Resolver<F> {

	public final Action<F, ?> target;
	public final Action<F, ?> source;
	public final F fluent;
	private Change change;

	public Bind(Action<F, ?> source, Action<F, ?> target, F fluent) {
		this.source = source;
		this.target = target;
		this.fluent = fluent;

		change = null;
	}

	public Bind(Action<F, ?> source, Action<F, ?> target) {
		this(source, target, null);
	}

	@Override
	public boolean appliable(Solution solution) {
		return !solution.working().reachable(target, source) &&
				(fluent == null || (source.eff.meets(fluent) && target.pre.
				meets(fluent)));
	}

	@Override
	public void apply(Solution solution) {
		change = new Change(solution.working(), source, target);
		if (fluent != null) {
			if (!source.eff.meets(fluent) || !target.pre.meets(fluent)) {
				revert();
				throw new IllegalArgumentException(
						"Impossible to create lying link.");
			} else {
				CausalLink link = solution.working().addEdge(source, target);
				solution.working().addEdge(link.add(fluent));
			}

		}
	}

	@Override
	public void revert() {
		change.undo();
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 61 * hash + Objects.hashCode(this.target);
		hash = 61 * hash + Objects.hashCode(this.source);
		hash = 61 * hash + Objects.hashCode(this.fluent);
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
		final Bind<?> other = (Bind<?>) obj;
		if (!Objects.equals(this.target, other.target)) {
			return false;
		}
		if (!Objects.equals(this.source, other.source)) {
			return false;
		}
		if (!Objects.equals(this.fluent, other.fluent)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return source + " =[" + fluent + "]=> " + target;
	}

	@Override
	public State<F> provides() {
		return new State<>(fluent == null ? list() : list(fluent));
	}

	@Override
	public Collection<Change> changes() {
		return list(change);
	}

}
