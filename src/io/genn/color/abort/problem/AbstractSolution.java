/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.genn.color.abort.problem;

import io.genn.color.planning.algorithm.Flaw;
import io.genn.color.planning.problem.Plan;
import io.genn.color.planning.problem.Solution;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author antoine
 */
public class AbstractSolution implements Solution {
	
	public final Plan[] plans;
	private Boolean success;
	protected int level;
	public final Set<Flaw>[] nextFlaws;
	
	public AbstractSolution(int level) {
		this.level = level;
		this.plans = new Plan[level + 1];
		plans[level] = new Plan();
		this.nextFlaws = new Set[2];
		nextFlaws[0] = new HashSet<>();
		nextFlaws[1] = new HashSet<>();
	}
	
	public Plan next() {
		if (AbstractSolution.this.level() > 0) {
			if(plans[AbstractSolution.this.level()-1] == null)
				plans[AbstractSolution.this.level()-1] = new Plan(working());
		} else {
			throw new IllegalAccessError(
					"Can't access next plan when on level 0 !");
		}
		return plans[AbstractSolution.this.level()-1];
	}
	
	@Override
	public Plan working() {
		return plans[AbstractSolution.this.level()];
	}
	
	@Override
	public Plan plan() {
		if (success == null && plans.length > AbstractSolution.this.level() + 1) {
			return plans[AbstractSolution.this.level() + 1];
		}
		if (success) {
			return working();
		}
		return null;
	}
	
	@Override
	public boolean completed() {
		return success;
	}
	
	@Override
	public String toString() {
		return "@" + AbstractSolution.this.level() + " " + working();
	}
	
	@Override
	public boolean success() {
		return success;
	}
	
	@Override
	public void end(boolean success) {
		this.success = success;
	}

	/**
	 * @return the level
	 */
	@Override
	public int level() {
		return level;
	}

	/**
	 * @param level the level to set
	 */
	public void level(int level) {
		this.level = level;
	}
	
}
