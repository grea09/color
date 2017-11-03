/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.genn.color.planning.problem;

import io.genn.color.planning.domain.Action;
import io.genn.color.planning.domain.Domain;

/**
 *
 * @author antoine
 */
public class Problem {

	public Action initial;
	public Action goal;
	public Domain domain; // not including those above
	public Solution solution;

	public Problem(Action initial, Action goal, Domain domain) {
		this.initial = initial;
		this.goal = goal;
		this.domain = domain;
	}

	public boolean solved() {
		return solution.completed();
	}

	@Override
	public String toString() {
		return "problem : " + "<initial  " + initial +
				", goal " + goal + "\n\tdomain " + domain + "\n\tsolution " +
				solution +
				"\n>";
	}

}
