/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.lollipop.type.flaw;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import me.grea.antoine.lollipop.agenda.Agenda;
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
            if (fluent == 0 || edge.labels.contains(fluent) && edge.labels.size() == 1) {
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

    public Set<Flaw> invalidated(Agenda agenda, Problem problem) {
        Set<Flaw> invalidated = new HashSet<>();
        if (negative) {
            for (Flaw flaw : agenda) {
                if (flaw.needer.equals(source)) {
                    invalidated.add(flaw);
                }
            }
        } else {
            for (Flaw flaw : agenda) {
                if (flaw instanceof Threat) {
                    Action threatSource = problem.plan.getEdgeSource(((Threat) flaw).threatened);
                    Action threatTarget = problem.plan.getEdgeTarget(((Threat) flaw).threatened);
                    if ((source == threatTarget && target == ((Threat) flaw).breaker)
                            || (source == ((Threat) flaw).breaker && target == threatSource)) {
                        invalidated.add(flaw); // Removed solved threats
                    }
                }
                if (flaw instanceof Orphan && problem.plan.outDegreeOf(flaw.needer) != 0) {
                    invalidated.add(flaw);
                }
            }
        }
        return invalidated;
    }

    public Set<Flaw> related(Problem problem) {
        Set<Flaw> related = new HashSet<>();
        if (negative) {
            if (edge == null) {
                related.addAll(Orphan.related(source, problem));
            } else {
                related.addAll(SubGoal.related(target, problem));

                related.addAll(Orphan.related(source, problem));

                related.addAll(Threat.related(target, problem));
                related.addAll(Threat.related(source, problem));
            }

        } else if (sourceCreated) {
            related.addAll(SubGoal.related(source, problem));
            related.addAll(Threat.related(source, problem));
        }

        Log.d("Related flaws " + related);

        return related;
    }

    @Override
    public String toString() {
        return source + (negative ? " ×" : " -") + (fluent == 0 ? "☠" : fluent) + "> " + target;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.source);
        hash = 29 * hash + Objects.hashCode(this.target);
        hash = 29 * hash + this.fluent;
        hash = 29 * hash + (this.negative ? 1 : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Resolver other = (Resolver) obj;
        if (this.fluent != other.fluent) {
            return false;
        }
        if (this.negative != other.negative) {
            return false;
        }
        if (!Objects.equals(this.source, other.source)) {
            return false;
        }
        if (!Objects.equals(this.target, other.target)) {
            return false;
        }
        return true;
    }

}
