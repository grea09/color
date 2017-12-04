/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.genn.color.pop;

import io.genn.color.planning.algorithm.Flaw;
import io.genn.color.planning.algorithm.Resolver;
import io.genn.color.planning.algorithm.Solver;
import io.genn.color.planning.domain.Action;
import io.genn.color.planning.domain.fluents.Fluent;
import io.genn.color.planning.problem.Problem;
import io.genn.color.pop.flaws.SubGoal;
import io.genn.color.pop.flaws.Threat;
import io.genn.color.pop.resolvers.Bind;
import io.genn.color.pop.resolvers.InstanciatedBind;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import static me.grea.antoine.utils.collection.Collections.queue;
import static me.grea.antoine.utils.collection.Collections.set;
import me.grea.antoine.utils.log.Log;

/**
 *
 * @author antoine
 */
public class PopSolver extends Solver {

	protected PopSolver(Problem problem) {
		super(problem);
	}

	public PopSolver(Flaw flaw, Problem problem) {
		super(flaw, problem);
	}

	@Override
	protected <F extends Flaw> Deque<Resolver> solve(F flaw) {
		if (flaw instanceof SubGoal) {
			return PopSolver.this.solve((SubGoal) flaw);
		} else if (flaw instanceof Threat) {
			return PopSolver.this.solve((Threat) flaw);
		}
		throw new IllegalArgumentException("The flaw " + flaw + " of type " +
				flaw.getClass() + " is not handled by " + getClass());
	}

	protected Deque<Resolver> solve(SubGoal flaw) {
		Deque<Resolver> resolvers = new ArrayDeque<Resolver>() {
			@Override
			public boolean add(Resolver e) {
				if (contains(e)) {
					return false;
				}
				return super.add(e);
			}

		};
		resolvers.addAll(solve(problem.initial, flaw));
		for (Action step : problem.solution.working().vertexSet()) {
			if (!step.special()) {
				resolvers.addAll(solve(step, flaw));
			}
		}
		for (Action action : problem.domain) {
			if (action.level <= problem.solution.level()) {
				resolvers.addAll(solve(action, flaw));
			}
		}
		return resolvers;
	}

	protected <F extends Fluent<F, E>, E> Deque<Resolver<F>> solve(
			Action<F, E> provider, SubGoal<F> flaw) {
		Deque<Resolver<F>> resolvers = queue();

		Collection<Map<E, E>> unifications = set();
		Collection<Map<E, E>> provide = provider.eff.provide(flaw.fluent);
		if (provide != null) {
			unifications.addAll(provide);
			if (unifications.isEmpty()) {
				unifications.add(new HashMap<>());
			}
		}
		Collection<Map<E, E>> obtain = provider.eff.obtain(flaw.fluent);
		if (obtain != null) {
			unifications.addAll(obtain);
		}
		if (!unifications.isEmpty()) {
			Log.v("Unifications of " + provider + " to solve " + this +
					" are " + unifications);
		}

		for (Map<E, E> unification : unifications) {
			if (unification.isEmpty()) {
				resolvers.add(new Bind(provider, flaw.needer, flaw.fluent));
				continue;
			}
			Action<F, E> gProvider = provider.instanciate(unification);
			Action<F, E> gNeeder = ((Action<F, E>) flaw.needer).instanciate(
					unification);
			F gFluent = flaw.fluent.instanciate(unification);
			if (gProvider == null || gNeeder == null || gFluent == null) {
				continue;
			}

			if (gProvider == provider && gNeeder == flaw.needer) {
				resolvers.add(new Bind(provider, flaw.needer, flaw.fluent));
			} else {
				boolean replace = !(gProvider.equals(provider) ||
						!problem.solution.working().containsVertex(provider) ||
						problem.solution.working().containsVertex(gProvider));
				resolvers.add(new InstanciatedBind(provider, gProvider,
												   flaw.needer,
												   gNeeder, flaw.fluent, gFluent,
												   unification, replace));
			}
		}

		return resolvers;
	}

	protected Deque<Resolver> solve(Threat flaw) {
		Deque<Resolver> resolvers = new ArrayDeque<>();
		if (flaw.threatened.target() != problem.goal) {
//            Log.w("Can't demote after goal step !");
			resolvers.add(new Bind<>(flaw.threatened.target(), flaw.breaker));
		}
		if (flaw.threatened.source() != problem.initial) {
//            Log.w("Can't promote before initial step !");
			resolvers.add(new Bind<>(flaw.breaker, flaw.threatened.source()));
		}
		return resolvers;
	}

}
