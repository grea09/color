/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.genn.color.planning.problem;

import io.genn.color.planning.algorithm.pop.PopAgenda;
import io.genn.color.planning.domain.Action;
import io.genn.color.planning.domain.Domain;
import io.genn.color.planning.flaw.Agenda;

/**
 *
 * @author antoine
 */
public class Problem {

	public Action initial;
	public Action goal;
	public Domain domain; // not including those above
	public Plan plan;

	public Problem(Action initial, Action goal, Domain domain,
			Plan plan) {
		this.initial = initial;
		this.goal = goal;
		this.domain = domain;
		this.plan = plan;
		this.plan.addEdge(initial, goal);
	}

	public Problem(Action initial, Action goal, Domain domain) {
		this(initial, goal, domain, new Plan());
	}

	public boolean solved() {
		Agenda agenda = new PopAgenda(this);
		return agenda.isEmpty();
	}

	@Override
	public String toString() {
		return "problem :: Problem\nproblem : " + "<initial  " + initial +
				", goal " + goal + "\n\tdomain " + domain + "\n\tplan " + plan +
				"\n>";
	}

}
