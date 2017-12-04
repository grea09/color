/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.genn.color.pop.resolvers;

import io.genn.color.planning.algorithm.Change;
import io.genn.color.planning.algorithm.Instanciation;
import io.genn.color.planning.algorithm.Resolver;
import io.genn.color.planning.domain.Action;
import io.genn.color.planning.domain.State;
import io.genn.color.planning.domain.fluents.Fluent;
import io.genn.color.planning.problem.CausalLink;
import io.genn.color.planning.problem.Plan;
import io.genn.color.planning.problem.Solution;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import me.grea.antoine.utils.log.Log;

/**
 *
 * @author antoine
 */
public class InstanciatedBind<F extends Fluent<F, E>, E> extends Bind<F> { //implements Resolver<F>

	protected final Action<F, ?> liftedSource;
//	public final Action<F, ?> source;
	protected final Action<F, ?> liftedTarget;
//	public final Action<F, ?> target;
	protected final F liftedFluent;
//	public final F fluent;
	protected final Set<Change> changes;
	public final Map<E, E> unification;
	public final boolean replace;
	private Set<Instanciation> instanciations;

	public InstanciatedBind(
			Action<F, ?> liftedSource,
			Action<F, ?> source,
			Action<F, ?> liftedTarget,
			Action<F, ?> target, F liftedFluent, F fluent,
			Map<E, E> unification, boolean replaceSource) {
		super(source,target, fluent);
		this.liftedSource = liftedSource;
//		this.source = source;
		this.liftedTarget = liftedTarget;
//		this.target = target;
		this.liftedFluent = liftedFluent;
//		this.fluent = fluent;
		this.unification = unification;
		this.changes = new HashSet<>();
		this.replace = replaceSource;
	}

	@Override
	public boolean appliable(Solution solution) {
		if (source == null || target == null || fluent == null) {
			return false;
		}
		if (replace && !solution.working().containsVertex(liftedSource)) {
			return false;
		}
		instanciations = new HashSet<>();
		if (replace && liftedSource != source) {
			Instanciation instanciation = new Instanciation(liftedSource,
															unification,
															solution.working());
			if (!instanciation.search()) {
				return false;
			}
			instanciations.add(instanciation);
		}
		if (liftedTarget != target &&
				solution.working().containsVertex(liftedTarget) &&
				!solution.working().containsVertex(target)) {
			Instanciation instanciation = new Instanciation(liftedTarget,
															unification,
															solution.working());
			if (!instanciation.search()) {
				return false;
			}
			instanciations.add(instanciation);
		}
		return !solution.working().reachable(target, source) &&
				!solution.working().reachable(target, liftedSource) &&
				!solution.working().reachable(liftedTarget, liftedSource) &&
				(fluent == null ||
				(source.eff.meets(fluent) && target.pre.meets(fluent)));
	}

	@Override
	public void apply(Solution solution) {
		for (Instanciation instanciation : instanciations) {
			changes.addAll(instanciation.apply());
		}
		changes.add(new Change(solution.working(), source, target));
		CausalLink link = solution.working().addEdge(source, target);
		solution.working().addEdge(link.add(fluent));
	}

	@Override
	public void revert() {
		for (Change change : changes) {
			change.undo();
		}
	}

	@Override
	public Collection<Change> changes() {
		return changes;
	}

	@Override
	public State<F> provides() {
		return new State<>(fluent);
	}

	@Override
	public String toString() {
		return source + " =[" + fluent + "]=> " + target;
	}

}
