/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.genn.color.planning.algorithm.pop.flaws;

import io.genn.color.planning.algorithm.Change;
import io.genn.color.planning.algorithm.CompositeResolver;
import io.genn.color.planning.domain.Action;
import io.genn.color.planning.domain.fluents.Fluent;
import io.genn.color.planning.domain.State;
import io.genn.color.planning.algorithm.Flaw;
import io.genn.color.planning.algorithm.Resolver;
import io.genn.color.planning.algorithm.pop.resolvers.Bind;
import io.genn.color.planning.algorithm.pop.resolvers.Instanciate;
import io.genn.color.planning.plan.CausalLink;
import io.genn.color.planning.domain.Problem;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import static me.grea.antoine.utils.collection.Collections.*;

/**
 *
 * @author antoine
 */
public class PopSubGoal<F extends Fluent<F, E>, E> extends Flaw<F> {

	public PopSubGoal(Problem problem) {
		super(null, null, problem);
	}

	public PopSubGoal(F fluent, Action<F, E> needer, Problem problem) {
		super(fluent, needer, problem);
	}

	@Override
	public Deque<Resolver<F>> resolvers() {
		Deque<Resolver<F>> resolvers = new ArrayDeque<Resolver<F>>() {
			@Override
			public boolean add(Resolver e) {
				if (contains(e)) {
					return false;
				}
				return super.add(e); //To change body of generated methods, choose Tools | Templates.
			}

		};
		resolvers.addAll(solve(problem.initial));
		for (Action<F, E> step : problem.plan.vertexSet()) {
			if (!step.special()) {
				resolvers.addAll(solve(step));
			}
		}
		for (Action<F, E> action : problem.domain) {
			resolvers.addAll(solve(action));
		}
		return resolvers;
	}

	public Deque<Resolver<F>> solve(Action<F, E> provider) {
		Deque<Resolver<F>> resolvers = queue();
		
		if (provider == null) {
			return resolvers;
		}
		Collection<Map<E, E>> unifications = set();
		if (provider.eff != null) {
			Collection<Map<E, E>> unify = provider.eff.provide(fluent);
			if (unify != null) {
				unifications.addAll(unify);
				resolvers.addAll(fill(provider, unifications));
			}
			unifications.clear();
//			for (F fluent : needer.pre) { //FIXME check order and state/fluents
			unify = provider.eff.obtain(fluent);
			if (unify != null) {
				unifications.addAll(unify);
				resolvers.addAll(fill(provider, unifications));
			}
//			}
//			fill((Action<F, E>) needer, unifications, resolvers);
		}
		return resolvers;
	}

	private Deque<Resolver<F>> fill(Action<F, E> provider, Collection<Map<E, E>> unifications) {
		Deque<Resolver<F>> resolvers = queue();
		for (Map<E, E> unification : unifications) {
			Action<F, E> groundedProvider = provider.instanciate(unification);
			Action<F, E> groundedNeeder = ((Action<F, E>) needer)
					.instanciate(unification);
			F groundedFluent = fluent.instanciate(unification);
			if (groundedProvider != null && groundedNeeder != null &&
					groundedFluent != null &&
					!groundedNeeder.equals(groundedProvider)) {
				CompositeResolver<F> composite = new CompositeResolver<>();
				if (!groundedProvider.equals(provider)) {
					composite.add(new Instanciate<>(
							provider, groundedProvider, unification));
				}

				if (!groundedNeeder.equals(needer)) {
					composite.add(new Instanciate<>(
							(Action<F, E>) needer, groundedNeeder, unification));
				}

				if (!composite.isEmpty()) {
					composite.add(new Bind<>(
							groundedProvider,
							groundedNeeder,
							groundedFluent));
					resolvers.add(composite);
				}
			}
		}
		if (resolvers.isEmpty()) {
			resolvers.add(new Bind<>(provider, needer,
									 fluent));
		}
		return resolvers;
	}

	@Override
	public Set<PopSubGoal<F, E>> related(Resolver<F> resolver) {
		Set<PopSubGoal<F, E>> related = new HashSet<>();
		for (Change change : resolver.changes()) {
//			if (!change.sourceExists) {
			related.addAll(related(change.source));
//			}
//			if (!change.targetExists) {
			related.addAll(related(change.target));
//			}
		}
		return related;
	}

	public Set<PopSubGoal<F, E>> related(Action<F, E> annoyer) {
		if (annoyer.initial()) {
			return set();
		}
		State<F> open = new State<>(annoyer.pre, false);
		if (problem.plan.containsVertex(annoyer)) {
			if (!annoyer.goal() && (problem.plan.outDegreeOf(annoyer) == 0 ||
					!problem.plan.reachable(annoyer, problem.goal))) {
				return set();
			}

			for (CausalLink link : problem.plan.incomingEdgesOf(annoyer)) {
				open.removeAll(link.causes);
			}
		} else {
			return new HashSet<>();
		}
		Set<PopSubGoal<F, E>> related = new HashSet<>();
		for (F f : open) {
			if (!f.control().discard(f)) {
				related.add(new PopSubGoal<>(f, annoyer, problem));
			}
		}
		return related;
	}

	@Override
	public Set<PopSubGoal<F, E>> flaws() {
		Set<PopSubGoal<F, E>> flaws = new HashSet<>();
		for (Action<F, E> action : problem.plan.vertexSet()) {
			flaws.addAll(related(action));
		}
		return flaws;
	}

	@Override
	public boolean invalidated(Resolver<F> resolver) {
		if (problem.plan.containsVertex(needer)) {
				return true;
			}
		for (Change change : resolver.changes()) {
			if (change.source.equals(needer) &&
					change.sourceDelete ||
					change.target.equals(needer) &&
					change.targetDelete) {
				return true;
			}
			if (change.target.equals(needer) &&
					resolver.provides().meets(fluent)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return fluent + " -> " + needer;
	}
	
	
	

}
