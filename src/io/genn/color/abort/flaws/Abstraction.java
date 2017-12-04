/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.genn.color.abort.flaws;

import io.genn.color.planning.algorithm.Flaw;
import io.genn.color.planning.algorithm.Resolver;
import io.genn.color.planning.domain.Action;
import io.genn.color.planning.domain.fluents.Fluent;
import io.genn.color.planning.problem.Problem;
import java.util.Deque;
import java.util.Set;

/**
 *
 * @author antoine
 */
public class Abstraction<F extends Fluent<F,?>> extends Flaw<F>{

	public Abstraction(Action<F, ?> abstracted) {
		super(null, abstracted); //FIXME do like threats and add an action + needer
	}

	@Override
	public String toString() {
		return needer + "…";
	}
	
	
}
