/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.soda.type.defect;

import java.util.HashSet;
import java.util.Set;
import me.grea.antoine.soda.type.Action;
import me.grea.antoine.soda.type.Edge;
import me.grea.antoine.soda.type.Problem;
import static me.grea.antoine.soda.utils.Collections.set;

/**
 *
 * @author antoine
 */
public class RedundantLink extends Defect {

    public Edge redundant;

    public RedundantLink(Edge redundant, Problem problem) {
        super(problem);
        this.redundant = redundant;
    }

    @Override
    public void fix() {
        problem.plan.removeEdge(redundant);
    }

    public static Set<RedundantLink> find(Problem problem) {
        Set<RedundantLink> result = new HashSet<>();
        for (Edge edge : problem.plan.edgeSet()) {
            RedundantLink redundantLink = is(edge, problem);
            if (redundantLink != null) {
                result.add(redundantLink);
            }
        }
        return result;
    }

    public static RedundantLink is(Edge edge, Problem problem) {
        if (edge.labels.isEmpty()) {
            Action source = problem.plan.getEdgeSource(edge);
            Action target = problem.plan.getEdgeTarget(edge);
//            problem.plan.removeEdge(edge);
            if (problem.plan.reachable(source, target, set(edge))) {
                return new RedundantLink(edge, problem);
            }
//            problem.plan.addEdge(source, target, edge);
        }
        return null;
    }

    @Override
    public String toString() {
        return "Link " + redundant + " is redundant";
    }

}
