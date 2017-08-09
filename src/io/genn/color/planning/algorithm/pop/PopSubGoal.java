/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.genn.color.planning.algorithm.pop;

import io.genn.color.planning.domain.Action;
import io.genn.color.planning.domain.Fluent;
import io.genn.color.planning.domain.State;
import io.genn.color.planning.flaw.Flaw;
import io.genn.color.planning.flaw.Resolver;
import io.genn.color.planning.problem.CausalLink;
import io.genn.color.planning.problem.Problem;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author antoine
 */
public class PopSubGoal<F extends Fluent<F, ?>> extends Flaw<F> {

	public PopSubGoal(Problem problem) {
		super(null, null, problem);
	}

	public PopSubGoal(F fluent, Action<F, ?> needer, Problem problem) {
		super(fluent, needer, problem);
	}

	@Override
	public Deque<Resolver> resolvers() {
		Deque<Resolver> resolvers = new ArrayDeque<Resolver>() {
			@Override
			public boolean add(Resolver e) {
				if (contains(e)) {
					return false;
				}
				return super.add(e); //To change body of generated methods, choose Tools | Templates.
			}

		};
		fill(problem.initial, resolvers);
		for (Action<F, ?> step : problem.plan.vertexSet()) {
			if (!step.special()) {
				fill(step, resolvers);
			}
		}
		for (Action<F, ?> action : problem.domain) {
			fill(action, resolvers);
		}
		return resolvers;
	}

	public void fill(Action<F, ?> step, Deque<Resolver> resolvers) {
		Action<F, ?> instanciateFor = step.provide(fluent); //FIXME prevent instanciation when existing
		if (instanciateFor != null) {
			resolvers.add(new Resolver<>(instanciateFor, needer, fluent));
		}
	}

	@Override
	public Set<PopSubGoal<F>> related(Resolver resolver) {
		return related(resolver.source);
	}

	public Set<PopSubGoal<F>> related(Action<F, ?> annoyer) {
		State<F> open = new State<>(annoyer.pre, false);
		if (problem.plan.containsVertex(annoyer)) {
			for (CausalLink link : problem.plan.incomingEdgesOf(annoyer)) {
				open.removeAll(link.causes);
			}
		} else {
			return new HashSet<>();
		}
		Set<PopSubGoal<F>> related = new HashSet<>();
		for (F f : open) {
			related.add(new PopSubGoal<>(f, annoyer, problem));
		}
		return related;
	}

	@Override
	public Set<PopSubGoal<F>> flaws() {
		Set<PopSubGoal<F>> flaws = new HashSet<>();
		for (Action<F, ?> action : problem.plan.vertexSet()) {
			flaws.addAll(related(action));
		}
		return flaws;
	}

	@Override
	public boolean invalidated(Resolver resolver) {
		return resolver.target == needer && resolver.fluent != null && fluent.
				unifies((F) resolver.fluent);
		//FIXME In Lollipop we need negative too
	}

	@Override
	public String toString() {
		return fluent + " -> " + needer;
	}

}
