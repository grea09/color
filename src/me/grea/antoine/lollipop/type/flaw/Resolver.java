/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.lollipop.type.flaw;

import java.util.HashSet;
import java.util.Set;
import me.grea.antoine.lollipop.type.Action;
import me.grea.antoine.lollipop.type.Edge;
import me.grea.antoine.lollipop.type.Plan;
import me.grea.antoine.lollipop.type.Problem;
import static me.grea.antoine.utils.Collections.*;
import me.grea.antoine.utils.Log;

/**
 *
 * @author antoine
 */
public class Resolver {

    public Action source;
    public Action target;
    public int fluent;
    public boolean negative = false;
    private Edge edge;
    private boolean edgeCreated = false;
    private boolean sourceCreated = false;
    private boolean targetCreated = false;
    private final Set<Action> posibleOrphans = new HashSet<>();

    public Resolver(Action source, Action target, int fluent, boolean negative) {
        this.source = source;
        this.target = target;
        this.fluent = fluent;
        this.negative = negative;
    }

    public Resolver(Action source, Action target, int fluent) {
        this(source, target, fluent, false);
    }

    public Resolver(Action source, Action target) {
        this(source, target, 0);
    }

    public void apply(Plan plan) {
        if (negative) {
            edge = plan.getEdge(source, target);
            if (edge == null) {
                Orphan.add(source, new HashSet<Action>() { // Ghooosssttt
                    {
                        for (Edge child : plan.incomingEdgesOf(source)) {
                            add(plan.getEdgeSource(child));
                        }
                    }
                });
                plan.removeVertex(source);
                return;
            }
            if (edge.labels.equals(set(fluent))) {
                plan.removeEdge(edge);
            } else {
                edge.labels.remove(fluent);
            }
            return;
        }
        if (fluent == 0 && plan.reachable(source, target)) { // No need to resolve the threat
            return;
        }

        sourceCreated = plan.addVertex(source);
        targetCreated = plan.addVertex(target);
        edge = plan.addEdge(source, target);
        edgeCreated = edge != null;
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
        if (negative) {
            if (edge == null) {
                plan.addVertex(source);
            } else {
                plan.addEdge(source, target, edge);
                edge.labels.add(fluent);
            }
            return;
        }

        if (edgeCreated) {
            plan.removeEdge(edge);
        }
        if (targetCreated) {
            plan.removeVertex(target);
        }
        if (sourceCreated) {
            plan.removeVertex(source);
        }
    }

    public boolean appliable(Plan plan) {
        return negative
                ? (plan.containsEdge(source, target) || plan.containsVertex(source))
                : //((!plan.containsEdge(source, target)
//                || !plan.getEdge(source, target).labels.contains(fluent))
//                && 
                !plan.reachable(target, source);
    }

    public Set<Flaw> related(Problem problem) {
        Set<Flaw> related = new HashSet<>();
//        if (negative) {
//            if (target != null) { //FIXME redo that
//                related.addAll(SubGoal.related(target, problem));
//            }
//            related.addAll(Orphan.related(source, problem));
//        } else {
        related.addAll(SubGoal.related(source, problem));
        related.addAll(Threat.related(source, problem));
//        }
        Log.d("Related flaws " + related);

        return related;
    }

    @Override
    public String toString() {
        return source + (negative ? " ×" : " -") + (fluent == 0 ? "☠" : fluent) + "> " + target;
    }

}
