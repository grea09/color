/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.soda.type.defect;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import me.grea.antoine.log.Log;
import me.grea.antoine.soda.type.Action;
import me.grea.antoine.soda.type.Edge;
import me.grea.antoine.soda.type.Problem;
import static me.grea.antoine.soda.utils.Collections.difference;
import static me.grea.antoine.soda.utils.Collections.intersection;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.KShortestPaths;

/**
 *
 * @author antoine
 */
public class RedundantLink extends Defect {

    public Edge redundant;
    public GraphPath<Action, Edge> instead;

    public RedundantLink(Edge redundant, GraphPath<Action, Edge> instead, Problem problem) {
        super(problem);
        this.redundant = redundant;
        this.instead = instead;
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
            List<GraphPath<Action, Edge>> paths = new KShortestPaths<>(problem.plan, problem.plan.getEdgeSource(edge), 2).getPaths(problem.plan.getEdgeTarget(edge));
            if (paths.size() > 1) {
                return new RedundantLink(edge, paths.get(1), problem);
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "Link " + redundant + " is redundant with " + instead;
    }

}
