/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.genn.color.hipop;

import io.genn.color.heart.flaws.Abstraction;
import io.genn.color.planning.algorithm.Agenda;
import io.genn.color.planning.algorithm.Change;
import io.genn.color.planning.algorithm.Flaw;
import io.genn.color.planning.domain.Action;
import io.genn.color.planning.problem.Problem;
import io.genn.color.pop.PopAgenda;
import java.util.Set;

/**
 *
 * @author antoine
 */
public class HiPopAgenda extends PopAgenda{
	
	public HiPopAgenda(Agenda other) {
		super(other);
	}

	public HiPopAgenda(Problem problem) {
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
			if (flaw instanceof Abstraction) {
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
