/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.genn.color.pop;

import io.genn.color.pop.flaws.SubGoal;
import io.genn.color.pop.flaws.Threat;
import java.util.ArrayList;
import io.genn.color.planning.algorithm.Agenda;
import io.genn.color.planning.algorithm.Change;
import io.genn.color.planning.algorithm.Flaw;
import io.genn.color.planning.algorithm.Resolver;
import io.genn.color.planning.domain.Action;
import io.genn.color.planning.domain.State;
import io.genn.color.planning.domain.fluents.Fluent;
import io.genn.color.planning.problem.CausalLink;
import io.genn.color.planning.problem.Problem;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import me.grea.antoine.utils.collection.Collections;
import static me.grea.antoine.utils.collection.Collections.set;
import me.grea.antoine.utils.log.Log;
import me.grea.antoine.utils.random.Dice;

/**
 *
 * @author antoine
 */
public class PopAgenda extends Agenda {

	public PopAgenda(Agenda other) {
		super(other);
	}

	public PopAgenda(Problem problem) {
		super(problem);
	}

	@Override
	protected void populate() {
		for (Action action : problem.solution.working().vertexSet()) {
			addAll(subgoals(action));
		}
		for (CausalLink causalLink : problem.solution.working().edgeSet()) {
			addAll(threats(causalLink));
		}
	}

	protected <F extends Fluent<F, E>, E> Set<SubGoal<F>> subgoals(
			Action<F, E> annoyer) {
		if (annoyer.initial() && annoyer.parameters == null) {
			return Collections.set();
		}
		State<F> open = new State<>(annoyer.pre, false);
		if (problem.solution.working().containsVertex(annoyer)) {
			if (!(annoyer.goal() && annoyer.parameters == null) &&
					(problem.solution.working().outDegreeOf(
							annoyer) == 0 ||
					!problem.solution.working().reachable(annoyer, problem.goal))) {
				return Collections.set();
			}

			for (CausalLink link : problem.solution.working().incomingEdgesOf(
					annoyer)) {
				open.removeAll(link.causes);
			}
		} else {
			return new HashSet<>();
		}
		Set<SubGoal<F>> related = new HashSet<>();
		for (F f : open) {
			if (!f.control().discard(f)) {
				related.add(new SubGoal<>(f, annoyer));
			}
		}
		return related;
	}

	protected <F extends Fluent<F, ?>> Set<Threat<F>> threats(
			Action<F, ?> breaker) {
		Set<Threat<F>> related = new HashSet<>();
		for (CausalLink fragile : problem.solution.working().edgeSet()) {
			related.addAll(threats(fragile, breaker));
		}
		return related;
	}

	protected <F extends Fluent<F, ?>> Set<Threat<F>> threats(CausalLink fragile) {
		Set<Threat<F>> related = new HashSet<>();
		for (Action breaker : problem.solution.working().vertexSet()) {
			related.addAll(threats(fragile, breaker));
		}
		return related;
	}

	protected <F extends Fluent<F, ?>> Set<Threat<F>> threats(CausalLink fragile,
			Action<F, ?> breaker) {
		Set<Threat<F>> related = new HashSet<>();
		if (fragile.target().equals(breaker) || fragile.source().equals(breaker)) {
			return related;
		}
		for (F cause : (State<F>) fragile.causes) {
			if (breaker.eff.contradicts(cause) && breaker != fragile.
					source() &&
					breaker != fragile.target() &&
					(!problem.solution.working().reachable(fragile.target(),
														   breaker) &&
					!problem.solution.working().reachable(breaker, fragile.
														  source()))) {
				related.add(new Threat(cause, breaker, fragile));
			}
		}
		return related;
	}

	@Override
	public Flaw choose() {
		Flaw result = Dice.pick(this); //Be random
		remove(result);
		return result;
	}

	@Override
	public void update(Resolver resolver) {
		Set<Flaw> related = new HashSet<>();
		Set<Flaw> invalidated = new HashSet<>();
		for (Change change : (Collection<Change>) resolver.changes()) {
			related.addAll(related(change));
			invalidated.addAll(invalidated(change));
		}
		related.removeAll(this);
		Log.v("New flaws : " + related);
		Log.v("Invalidated flaws : " + invalidated);
		addAll(related);
		removeAll(invalidated);
	}

	protected Set<Flaw> related(Change change) {
		Set<Flaw> related = new HashSet<>();
		related.addAll(subgoals(change.source));
		related.addAll(subgoals(change.target));
		if (change.link == null) {
			CausalLink link =
					problem.solution.working().edge(change.source,
													change.target);
			if (link != null) {
				related.addAll(threats(link));
			}
		}
		return related;
	}

	protected Set<Flaw> invalidated(Change change) {
		Set<Flaw> invalidated = new HashSet<>();
		for (Flaw flaw : this) {
			if (flaw instanceof SubGoal) {
				SubGoal subgoal = (SubGoal) flaw;
				Set<CausalLink> participating =
						problem.solution.working().edgesOf(subgoal.needer);
				if (participating != null) {
					for (CausalLink link : participating) {
						if (link.causes != null && link.causes.
								contains(subgoal.fluent)) {
							invalidated.add(subgoal);
						}
					}
				} else if (!problem.solution.working().containsVertex(
						subgoal.needer)) {
					invalidated.add(subgoal);
				}
			}
			if (flaw instanceof Threat) {
				Threat threat = (Threat) flaw;
				if (change.link == null &&
						(problem.solution.working().reachable(threat.threatened.
								target(), change.source) &&
						problem.solution.working().reachable(change.target,
															 threat.breaker)) ||
						(problem.solution.working().reachable(threat.breaker,
															  change.source) &&
						problem.solution.working().reachable(change.target,
															 threat.threatened.
																	 source()))) {
					invalidated.add(flaw);
				} else if (!problem.solution.working().containsEdge(
						threat.threatened) || !problem.solution.working().
								containsVertex(threat.breaker)) {
					invalidated.add(flaw);
				}
			}

		}
		return invalidated;
	}
}
