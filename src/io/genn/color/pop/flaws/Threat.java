/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the reverter.
 */
package io.genn.color.pop.flaws;

import io.genn.color.planning.algorithm.Change;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;
import io.genn.color.planning.domain.Action;
import io.genn.color.planning.domain.fluents.Fluent;
import io.genn.color.planning.domain.State;
import io.genn.color.planning.algorithm.Flaw;
import io.genn.color.planning.algorithm.Resolver;
import io.genn.color.pop.resolvers.Bind;
import io.genn.color.planning.problem.CausalLink;
import io.genn.color.planning.problem.Problem;
import static me.grea.antoine.utils.collection.Collections.union;

/**
 *
 * @author antoine
 */
public class Threat<F extends Fluent<F, ?>> extends Flaw<F> {

	public final CausalLink threatened;
	public final Action breaker;

	public Threat(F fluent, Action<F, ?> breaker, CausalLink threatened) {
		super(fluent, threatened == null ? null : threatened.target());
		this.breaker = breaker;
		this.threatened = threatened;
	}

	@Override
	public String toString() {
		return threatened + " # " + breaker;
	}

}
