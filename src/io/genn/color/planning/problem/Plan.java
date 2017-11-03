/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.genn.color.planning.problem;

import io.genn.color.planning.problem.CausalLink;
import io.genn.color.planning.domain.Action;
import io.genn.color.planning.domain.fluents.Fluent;
import me.grea.antoine.utils.graph.Graph;

/**
 *
 * @author antoine
 */
public class Plan extends Graph<Action, CausalLink> {

	public Plan() {
		super(CausalLink.class, true); // Java is an @$$#?!3 with type errasure !
	}

	public Plan(Graph other) {
		super(other);
	}

	@Override
	public String toString() {
		String edges = "";
		return edgeSet().stream().map(
				(edge) ->
				"\t" + edge + "\n")
				.reduce(edges, String::concat);
	}

}
