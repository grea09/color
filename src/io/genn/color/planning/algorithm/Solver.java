/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.genn.color.planning.algorithm;

import io.genn.color.planning.domain.Action;
import io.genn.color.planning.problem.Problem;
import io.genn.color.pop.resolvers.Bind;
import io.genn.color.pop.resolvers.InstanciatedBind;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import static me.grea.antoine.utils.collection.Collections.queue;
import static me.grea.antoine.utils.collection.Collections.set;
import me.grea.antoine.utils.log.Log;

/**
 *
 * @author antoine
 */
public abstract class Solver extends ArrayDeque<Resolver> {

	public final Problem problem;
	
	protected Solver(Problem problem) {
		this.problem = problem;
	}
	
	public Solver(Flaw flaw, Problem problem) {
		this.problem = problem;
		addAll(solve(flaw));
	}
	
	protected abstract <F extends Flaw> Deque<Resolver> solve(F flaw);
}
