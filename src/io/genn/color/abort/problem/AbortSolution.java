/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.genn.color.abort.problem;

import io.genn.color.planning.problem.Plan;
import io.genn.color.planning.problem.Solution;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author antoine
 */
public class AbortSolution implements Solution {

	public final List<Plan> plans;
	public int level;
	private Boolean success;

	public AbortSolution(int level) {
		this.level = level;
		this.plans = new ArrayList<>(level);
	}

	public AbortSolution(Plan plan) {
		this(0);
		plans.set(level, plan);
	}

	@Override
	public Plan working() {
		return plans.get(level);
	}

	@Override
	public Plan plan() {
		if (success == null && plans.size() > level + 1) {
			return plans.get(level + 1);
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
		return "@" + level + " " + working();
	}

	@Override
	public boolean success() {
		return success;
	}

	@Override
	public void end(boolean success) {
		this.success = success;
	}

}
