/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.genn.color.hipop.problem;

import io.genn.color.planning.problem.Plan;
import io.genn.color.pop.problem.SimpleSolution;

/**
 *
 * @author antoine
 */
public class CompositeSolution extends SimpleSolution{
	
	private int level;
	
	public CompositeSolution(Plan plan, int level) {
		super(plan);
		this.level = level;
	}

	public CompositeSolution(int level) {
		this.level = level;
	}

	@Override
	public int level() {
		return level;
	}
	
	public void level(int level) {
		this.level = level;
	}
	
	
}
