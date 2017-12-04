/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.genn.color.pop.problem;

import io.genn.color.planning.problem.Plan;
import io.genn.color.planning.problem.Solution;

/**
 *
 * @author antoine
 */
public class SimpleSolution implements Solution{
	
	private Plan plan;
	private Boolean success;

	public SimpleSolution(Plan plan) {
		this.plan = plan;
	}

	public SimpleSolution() {
		this(new Plan());
	}

	@Override
	public Plan working() {
		return plan;
	}

	@Override
	public Plan plan() {
		if(success)
			return plan;
		return null;
	}

	@Override
	public boolean completed() {
		return success!=null;
	}

	@Override
	public void end(boolean success) {
		this.success = success;
	}

	@Override
	public boolean success() {
		return success;
	}

	@Override
	public String toString() {
		return plan.toString();
	}

	@Override
	public int level() {
		return 0;
	}
	
	
	
}
