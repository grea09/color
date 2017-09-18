/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.genn.color.planning.algorithm;

import io.genn.color.planning.domain.Action;
import io.genn.color.planning.plan.CausalLink;
import io.genn.color.planning.plan.CausalLink;
import io.genn.color.planning.plan.Plan;
import io.genn.color.planning.plan.Plan;

/**
 *
 * @author antoine
 */
public class Change {

		public final boolean sourceDelete;
		public final boolean targetDelete;
		public final boolean sourceExists;
		public final boolean targetExists;
		public final Action source;
		public final CausalLink link;
		public final Action target;

		private final Plan plan;

		public Change(Plan plan, Action source, Action target) {
			this(plan, source, target, false, false);
		}
		public Change(Plan plan, Action source, Action target, boolean sourceDelete, boolean targetDelete) {
			this.source = source;
			this.target = target;
			this.plan = plan;
			this.sourceDelete = sourceDelete;
			this.targetDelete = targetDelete;
			this.sourceExists = plan.containsVertex(source);
			this.targetExists = plan.containsVertex(target);
			this.link = plan.edge(source, target);
		}
		
		public Change(Plan plan, CausalLink link) {
			this(plan, link, false, false);
		}
		public Change(Plan plan, CausalLink link, boolean sourceDelete, boolean targetDelete) {
			this.source = link.source();
			this.target = link.target();
			this.plan = plan;
			this.sourceDelete = sourceDelete;
			this.targetDelete = targetDelete;
			this.sourceExists = plan.containsVertex(source);
			this.targetExists = plan.containsVertex(target);
			this.link = link;
		}

		public void undo() {
			if (link != null) {
				plan.addEdge(link);
			} else {
				plan.removeEdge(source,target);
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
		return (sourceDelete?"-":"") + (sourceExists?"":"*") + source +
				" =" + (link== null ? "":"("+link.causes+")") + ">" +
				(targetDelete?"-":"") + (targetExists?"":"*") + target;
	}
		
		
	}
