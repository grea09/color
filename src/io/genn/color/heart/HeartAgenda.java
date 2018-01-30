/*

 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.genn.color.heart;

import io.genn.color.heart.flaws.Abstraction;
import io.genn.color.heart.problem.LeveledSolution;
import io.genn.color.heart.resolvers.Expand;
import io.genn.color.planning.algorithm.Agenda;
import io.genn.color.planning.algorithm.Change;
import io.genn.color.planning.algorithm.Flaw;
import io.genn.color.planning.algorithm.Resolver;
import io.genn.color.planning.domain.Action;
import io.genn.color.planning.problem.Problem;
import io.genn.color.pop.PopAgenda;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import me.grea.antoine.utils.log.Log;

/**
 *
 * @author antoine
 */
public class HeartAgenda extends PopAgenda {

	public HeartAgenda(Agenda other) {
		super(other);
	}

	public HeartAgenda(Problem problem) {
		super(problem);
	}

	@Override
	protected Set<Flaw> invalidated(Change change) {
		Set<Flaw> invalidated = super.invalidated(change);

		boolean sourceRemoved =
				!problem.solution.working().containsVertex(change.source) &&
				change.sourceExists;
		boolean targetRemoved =
				!problem.solution.working().containsVertex(change.target) &&
				change.targetExists;
		if (sourceRemoved || targetRemoved) {
			for (Flaw flaw : this) {
				if (flaw instanceof Abstraction && ((flaw.needer.equals(
						change.source) && sourceRemoved) ||
						(flaw.needer.equals(change.target) && targetRemoved))) {
					invalidated.add(flaw);
				}
			}
		}
		return invalidated;
	}
	
	@Override
	public void update(Resolver resolver) {
		Set<Flaw> related = new HashSet<>();
		Set<Flaw> invalidated = new HashSet<>();
		for (Change change : (Collection<Change>) resolver.changes()) {
			related.addAll(related(change));
			invalidated.addAll(invalidated(change));
		}
		if(resolver instanceof Expand)
		{
			Set<Flaw>[] changes =
						((LeveledSolution)problem.solution).nextFlaws;
			if(!((Expand)resolver).last)
			{
				changes[0].addAll(related);
				changes[1].addAll(invalidated);
				Log.v("Agenda update delayed for next Expansion flaws.");
				return;
			} else {
				related.addAll(changes[0]);
				invalidated.addAll(changes[1]);
				Log.v("Loaded retained flaws for level change.");
				changes[0].clear();
				changes[1].clear();
			}
		}
		related.removeAll(this);
		Log.v("New flaws : " + related);
		Log.v("Invalidated flaws : " + invalidated);
		addAll(related);
		removeAll(invalidated);
	}

	@Override
	protected Set<Flaw> related(Change change) {
		Set<Flaw> related = super.related(change);
		if (!change.sourceExists && change.source.level != 0) {
			related.add(new Abstraction(change.source));
		}
		if (!change.targetExists && change.target.level != 0) {
			related.add(new Abstraction(change.target));
		}
		return related;
	}

	@Override
	public Flaw choose() {
		for (Flaw flaw : this) {
			if (!(flaw instanceof Abstraction)) {
				remove(flaw);
				return flaw;
			}
		}
		return super.choose();
	}

	@Override
	protected void populate() {
		super.populate();
		for (Action action : problem.solution.working().vertexSet()) {
			if (action.level != 0) {
				add(new Abstraction(action));
			}
		}
	}

}
