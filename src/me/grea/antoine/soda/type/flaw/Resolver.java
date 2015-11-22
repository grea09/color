/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.soda.type.flaw;

import me.grea.antoine.soda.type.flaw.SubGoal;
import me.grea.antoine.soda.type.flaw.Threat;
import java.util.HashSet;
import java.util.Set;
import me.grea.antoine.log.Log;
import me.grea.antoine.soda.type.Action;
import me.grea.antoine.soda.type.Edge;
import me.grea.antoine.soda.type.Plan;
import me.grea.antoine.soda.type.Problem;
import org.jgrapht.DirectedGraph;
import static me.grea.antoine.soda.utils.Collections.*;

/**
 *
 * @author antoine
 */
public class Resolver {

    public Action source;
    public Action target;
    public int fluent;
    private Edge edge;

    public Resolver(Action source, Action target, int fluent) {
        this.source = source;
        this.target = target;
        this.fluent = fluent;
    }

    public void apply(Plan plan) {
        plan.addVertex(source);
        plan.addVertex(target);
        edge = plan.addEdge(source, target);
        if (fluent != 0) {
            if (edge == null) {
                edge = plan.getEdge(source, target);
                edge.labels.add(fluent);
            } else {
                edge.labels = set(fluent);
            }
        }
    }

    public void revert(Plan plan) {
        plan.removeEdge(edge);
        if (plan.outDegreeOf(target) == 0) {
            plan.removeVertex(target);
        }
        if (plan.outDegreeOf(source) == 0) {
            plan.removeVertex(source);
        }
    }

    public boolean duplicate(Plan plan) {
        return plan.containsEdge(source, target) && plan.getEdge(source, target).labels.contains(fluent);
    }

    public Set<Flaw> related(Problem problem) {
        Set<Flaw> related = new HashSet<>();
        related.addAll(SubGoal.related(source, problem));
        related.addAll(Threat.related(source, problem));
//        Log.d("Related flaws " + related);
        return related;
    }

    @Override
    public String toString() {
        return source + " =(" + (fluent == 0 ? "☠" : fluent) + ")> " + target;
    }

}
