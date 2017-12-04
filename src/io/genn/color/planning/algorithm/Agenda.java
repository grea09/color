/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.genn.color.planning.algorithm;

import java.util.ArrayList;
import io.genn.color.planning.problem.Problem;
import java.util.Collection;
import static me.grea.antoine.utils.collection.Collections.*;
import me.grea.antoine.utils.text.Formater;

/**
 *
 * @author antoine
 */
public abstract class Agenda extends ArrayList<Flaw> {

	protected final Problem problem;

	public Agenda(Agenda other) {
		super(other);
		problem = other.problem;
	}

	public Agenda(Problem problem) {
		this.problem = problem;
		populate();
	}

	protected abstract void populate();

	public abstract Flaw choose();

	public abstract void update(Resolver resolver);

	@Override
	public String toString() {
		return Formater.toString(this, "\n\t * ");
	}

	@Override
	public boolean add(Flaw e) {
		removeAll(list(e));
		return super.add(e);
	}

	@Override
	public boolean addAll(
			Collection<? extends Flaw> clctn) {
		boolean changed = false;
		if (clctn != null) {
			for (Flaw flaw : clctn) {
				changed |= add(flaw);
			}
		}
		return changed;
	}

}
