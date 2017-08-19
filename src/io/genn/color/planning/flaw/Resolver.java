/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.genn.color.planning.flaw;

import java.util.Objects;
import java.util.Set;
import io.genn.color.planning.domain.Action;
import io.genn.color.planning.domain.Fluent;
import io.genn.color.planning.domain.State;
import io.genn.color.planning.problem.CausalLink;
import io.genn.color.planning.problem.Plan;

/**
 *
 * @author antoine
 */
public class Resolver<F extends Fluent<F, ?>> {

	public final Action<F, ?> target;
	public final Action<F, ?> source;
	public final F fluent;
	protected Existing originalState;
	protected Set<CausalLink> orphans = null;
	public final boolean negative;
	private State<F> state;

	private class Existing {

		public final boolean source;
		public final boolean target;
		public final CausalLink link;

		private final Plan plan;

		public Existing(Plan plan) {
			this.source = plan.containsVertex(Resolver.this.source);
			this.target = plan.containsVertex(Resolver.this.target);
			this.link = plan.edge(Resolver.this.source, Resolver.this.target);
			this.plan = plan;
		}

		public void restore() {
			if (link != null) {
				plan.addEdge(link);
			} else {
				CausalLink toRemove = plan.addEdge(Resolver.this.source,
												   Resolver.this.target);
				if (source) {
					plan.addVertex(Resolver.this.source);
				} else {
					plan.removeVertex(Resolver.this.source);
				}
				if (target) {
					plan.addVertex(Resolver.this.target);
				} else {
					plan.removeVertex(Resolver.this.target);
				}
			}
		}
	}

	public Resolver(Action<F, ?> source, Action<F, ?> target, F fluent,
			boolean negative) {
		this.source = source;
		this.target = target;
		this.fluent = fluent;

		this.negative = negative;
		originalState = null;
	}

	public Resolver(Action<F, ?> source, Action<F, ?> target, F fluent) {
		this(source, target, fluent, false);
	}

	public Resolver(Action<F, ?> source, Action<F, ?> target) {
		this(source, target, null, false);
	}

	public boolean appliable(Plan plan) {
		return negative ? (plan.containsEdge(source, target) || (plan.
			   containsVertex(source) && target == null)) :
				 !plan.reachable(target, source) &&
			   (fluent == null || (source.eff.unifies(fluent) && target.pre.
			   unifies(fluent)));
	}

	public void apply(Plan plan) {
		originalState = new Existing(plan);
		if (negative) {
			if (target == null) {
				orphans = plan.incomingEdgesOf(source);
				plan.removeVertex(source);
				return;
			}
			CausalLink link = plan.edge(source, target);
			plan.removeEdge(link);
			if (link != null && fluent != null) {
				link = link.remove(fluent);
				if (!link.causes.isEmpty()) {
					plan.addEdge(link);
				}
			}

		} else {
			CausalLink link = plan.addEdge(source, target);
			if (fluent != null) {
				if (!source.eff.unifies(fluent) || !target.pre.unifies(fluent)) {
					revert();
					throw new IllegalArgumentException(
							"Impossible to create lying link.");
				} else {
					plan.addEdge(link.add(fluent));
				}
			}
		}
	}

	public void revert() {
		originalState.restore();
		if (orphans != null) {
			for (CausalLink orphan : orphans) {
				originalState.plan.addEdge(orphan);
			}
		}
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 61 * hash + Objects.hashCode(this.target);
		hash = 61 * hash + Objects.hashCode(this.source);
		hash = 61 * hash + Objects.hashCode(this.fluent);
		hash = 61 * hash + (this.negative ? 1 : 0);
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
		final Resolver<?> other = (Resolver<?>) obj;
		if (this.negative != other.negative) {
			return false;
		}
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

}
