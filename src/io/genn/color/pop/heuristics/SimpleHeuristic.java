/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.genn.color.pop.heuristics;

import io.genn.color.planning.algorithm.Heuristic;
import io.genn.color.planning.domain.Action;

/**
 *
 * @author antoine
 */
public class SimpleHeuristic implements Heuristic{

	@Override
	public int compare(Action a1, Action a2) {
		if(a1.initial())
			return -1;
		if(a2.initial())
			return 1;
		if(a2.level != a1.level)
			return a2.level - a1.level;
		return Double.compare(a1.cost, a2.cost);
	}
	
}
