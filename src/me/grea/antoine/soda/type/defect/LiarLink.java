/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.soda.type.defect;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import me.grea.antoine.log.Log;
import me.grea.antoine.soda.type.Action;
import me.grea.antoine.soda.type.Edge;
import me.grea.antoine.soda.type.Problem;
import static me.grea.antoine.soda.utils.Collections.difference;
import static me.grea.antoine.soda.utils.Collections.union;

/**
 *
 * @author antoine
 */
public class LiarLink extends Defect {

    public Edge liar;
    public Set<Integer> lies;
    public Set<Integer> saviours;

    public LiarLink(Edge liar, Set<Integer> lies, Set<Integer> saviours, Problem problem) {
        super(problem);
        this.liar = liar;
        this.lies = lies;
        this.saviours = saviours;
    }

    @Override
    public void fix() {
        liar.labels.removeAll(lies);
        liar.labels.addAll(saviours);
        if (liar.labels.isEmpty()) {
            problem.plan.removeEdge(liar);
        }
    }

    public static Set<LiarLink> find(Problem problem) {
        Set<LiarLink> result = new HashSet<>();
        for (Edge edge : problem.plan.edgeSet()) {
            LiarLink liarLink = is(edge, problem);
            if (liarLink != null) {
                result.add(liarLink);
            }
        }
        return result;
    }

    public static LiarLink is(Edge edge, Problem problem) {
        Set<Integer> common = union(
                problem.plan.getEdgeSource(edge).effects,
                problem.plan.getEdgeTarget(edge).preconditions);
        Set<Integer> lies = difference(edge.labels, common);
        if (lies.isEmpty()) {
            return null;
        }
        return new LiarLink(edge, lies, difference(common, edge.labels), problem);

    }

    @Override
    public String toString() {
        return  "Link " + liar + " is lying about " + lies + " without mentioning " + saviours;
    }

}
