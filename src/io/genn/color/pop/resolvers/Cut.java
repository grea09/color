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
@Deprecated
public class Cut<F extends Fluent<F, ?>> implements Resolver<F> {

	public final Action<F, ?> target;
	public final Action<F, ?> source;
	public final F fluent;
	protected Change change;
//	protected Set<CausalLink> orphans = null;

	public Cut(Action<F, ?> source, Action<F, ?> target, F fluent,
			boolean negative) {
		this.source = source;
		this.target = target;
		this.fluent = fluent;

		change = null;
	}

	public Cut(Action<F, ?> source, Action<F, ?> target, F fluent) {
		this(source, target, fluent, false);
	}

	public Cut(Action<F, ?> source, Action<F, ?> target) {
		this(source, target, null, false);
	}

	@Override
	public boolean appliable(Solution solution) {
		return (solution.working().containsEdge(source, target) || (solution.working().
				containsVertex(source) && target == null));
	}

	@Override
	public void apply(Solution solution) {
		change = new Change(solution.working(), source, target);
		if (target == null) {
//				orphans = plan.incomingEdgesOf(source);
			solution.working().removeVertex(source);
			return;
		}
		CausalLink link = solution.working().edge(source, target);
		solution.working().removeEdge(link);
		if (link != null && fluent != null) {
			link = link.remove(fluent);
			if (!link.causes.isEmpty()) {
				solution.working().addEdge(link);
			}
		}
	}

	@Override
	public void revert() {
		change.undo();
//		if (orphans != null) {
//			for (CausalLink orphan : orphans) {
//				change.plan.addEdge(orphan);
//			}
//		}
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
		final Cut<?> other = (Cut<?>) obj;
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
		return new State<>(list(fluent));
	}

	@Override
	public Collection<Change> changes() {
		return list(change);
	}

}
