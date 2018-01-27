/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.genn.color.planning.problem;

import io.genn.color.planning.domain.Action;
import io.genn.color.planning.domain.Domain;
import java.util.Objects;

/**
 *
 * @author antoine
 */
public class Problem implements Comparable<Problem>{

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

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 11 * hash + Objects.hashCode(this.initial);
		hash = 11 * hash + Objects.hashCode(this.goal);
		hash = 11 * hash + Objects.hashCode(this.domain);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Problem other = (Problem) obj;
		return true;
	}
	
	

	@Override
	public int compareTo(Problem t) {
		return hashCode() - t.hashCode();
	}

}
