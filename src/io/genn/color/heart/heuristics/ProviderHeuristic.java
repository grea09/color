/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.genn.color.heart.heuristics;

import io.genn.color.planning.algorithm.Resolver;
import io.genn.color.pop.resolvers.Bind;
import java.util.Comparator;

/**
 *
 * @author antoine
 */
public class ProviderHeuristic implements Comparator<Resolver>{

	@Override
	public int compare(Resolver r1, Resolver r2) {
		if(r1.equals(r2))
			return 0;
		if(r1 instanceof Bind && r2 instanceof Bind) {
			Bind b1 = (Bind) r1;
			Bind b2 = (Bind) r2;
			if(b1.source.initial())
				return -1;
			if(b2.source.initial())
				return 1;
			return b2.source.level - b1.source.level;//TODO add costs here too
		}
		throw new ClassCastException("The types of the compared resolvers aren't extending Bind.");
	}
	
}
