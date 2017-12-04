/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.genn.color.pop.flaws;

import io.genn.color.planning.algorithm.Change;
import io.genn.color.planning.domain.Action;
import io.genn.color.planning.domain.fluents.Fluent;
import io.genn.color.planning.domain.State;
import io.genn.color.planning.algorithm.Flaw;
import io.genn.color.planning.algorithm.Resolver;
import io.genn.color.pop.resolvers.Bind;
import io.genn.color.pop.resolvers.InstanciatedBind;
import io.genn.color.planning.problem.CausalLink;
import io.genn.color.planning.problem.Problem;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import static me.grea.antoine.utils.collection.Collections.*;
import me.grea.antoine.utils.log.Log;

/**
 *
 * @author antoine
 */
public class SubGoal<F extends Fluent<F, ?>> extends Flaw<F> {

	public SubGoal(F fluent, Action<F, ?> needer) {
		super(fluent, needer);
	}

	protected SubGoal(SubGoal<F> other) {
		super(other.fluent, other.needer);
	}

	@Override
	public String toString() {
		return fluent + " -> " + needer;
	}

}
