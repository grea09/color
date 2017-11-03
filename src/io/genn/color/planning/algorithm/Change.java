/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.genn.color.planning.algorithm;

import io.genn.color.planning.domain.Action;
import io.genn.color.planning.problem.CausalLink;
import io.genn.color.planning.problem.Plan;

/**
 *
 * @author antoine
 */
public class Change { //FIXME, reverting sometimes makes leftovers

	public final boolean sourceExists;
	public final boolean targetExists;
	public final Action source;
	public final CausalLink link;
	public final Action target;

	private final Plan plan;

	public Change(Plan plan, Action source, Action target) {
		this.source = source;
		this.target = target;
		this.plan = plan;

		this.sourceExists = plan.containsVertex(source);
		this.targetExists = plan.containsVertex(target);
		this.link = plan.edge(source, target);
	}

	public Change(Plan plan, CausalLink link) {
		this.source = link.source();
		this.target = link.target();
		this.plan = plan;
		this.sourceExists = plan.containsVertex(source);
		this.targetExists = plan.containsVertex(target);
		this.link = plan.containsEdge(link) ? link : null;
	}

	public void undo() {
		if (link != null) {
			plan.addEdge(link);
		} else {
			plan.removeEdge(source, target);
			if (sourceExists) {
				plan.addVertex(source);
			} else {
				plan.removeVertex(source);
			}
			if (targetExists) {
				plan.addVertex(target);
			} else {
				plan.removeVertex(target);
			}
		}
	}

	@Override
	public String toString() {
		return (sourceExists ? "" : "*") + source +
				" =" + (link == null ? "" : "(" + link.causes + ")") + ">" +
				(targetExists ? "" : "*") + target;
	}

}
