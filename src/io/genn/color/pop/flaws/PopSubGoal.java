/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.genn.color.pop.flaws;

import io.genn.color.planning.algorithm.Change;
import io.genn.color.planning.domain.Action;
import io.genn.color.planning.domain.fluents.Fluent;
import io.genn.color.planning.domain.State;
import io.genn.color.planning.algorithm.Flaw;
import io.genn.color.planning.algorithm.Resolver;
import io.genn.color.pop.resolvers.Bind;
import io.genn.color.pop.resolvers.InstanciatedBind;
import io.genn.color.planning.problem.CausalLink;
import io.genn.color.planning.problem.Problem;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import static me.grea.antoine.utils.collection.Collections.*;
import me.grea.antoine.utils.log.Log;

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
				return super.add(e);
			}

		};
		resolvers.addAll(solve(problem.initial));
		for (Action<F, E> step : problem.solution.working().vertexSet()) {
			if (!step.special()) {
				resolvers.addAll(solve(step));
			}
		}
		for (Action<F, E> action : problem.domain) {
			resolvers.addAll(solve(action));
		}
		return resolvers;
	}

	protected Deque<Resolver<F>> solve(Action<F, E> provider) {
		Deque<Resolver<F>> resolvers = queue();

		Collection<Map<E, E>> unifications = set();
		Collection<Map<E, E>> provide = provider.eff.provide(fluent);
		if (provide != null) {
			unifications.addAll(provide);
			if (unifications.isEmpty()) {
				unifications.add(new HashMap<>());
			}
		}
		Collection<Map<E, E>> obtain = provider.eff.obtain(fluent);
		if (obtain != null) {
			unifications.addAll(obtain);
		}
		if (!unifications.isEmpty()) {
			Log.v("Unifications of " + provider + " to solve " + this +
					" are " + unifications);
		}

		for (Map<E, E> unification : unifications) {
			if (unification.isEmpty()) {
				resolvers.add(new Bind(provider, needer, fluent));
				continue;
			}
			Action<F, E> gProvider = provider.instanciate(unification);
			Action<F, E> gNeeder = ((Action<F, E>) needer).instanciate(
					unification);
			F gFluent = fluent.instanciate(unification);
			if (gProvider == null || gNeeder == null || gFluent == null) {
				continue;
			}

			if (gProvider == provider && gNeeder == needer) {
				resolvers.add(new Bind(provider, needer, fluent));
			} else {
				boolean replace = !(gProvider.equals(provider) ||
						!problem.solution.working().containsVertex(provider) ||
						problem.solution.working().containsVertex(gProvider));
				resolvers.add(new InstanciatedBind(provider, gProvider, needer,
												   gNeeder, fluent, gFluent,
												   unification, replace));
			}
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
		if (problem.solution.working().containsVertex(annoyer)) {
			if (!annoyer.goal() && (problem.solution.working().outDegreeOf(
					annoyer) == 0 ||
					!problem.solution.working().reachable(annoyer, problem.goal))) {
				return set();
			}

			for (CausalLink link : problem.solution.working().incomingEdgesOf(
					annoyer)) {
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
		for (Action<F, E> action : problem.solution.working().vertexSet()) {
			flaws.addAll(related(action));
		}
		return flaws;
	}

	@Override
	public boolean invalidated(Resolver<F> resolver) {
		return false;
	}

	@Override
	public String toString() {
		return fluent + " -> " + needer;
	}

}
