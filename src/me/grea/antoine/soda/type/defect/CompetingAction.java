/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.soda.type.defect;

import java.util.HashSet;
import java.util.Set;
import me.grea.antoine.log.Log;
import me.grea.antoine.soda.type.Action;
import me.grea.antoine.soda.type.Edge;
import me.grea.antoine.soda.type.Problem;
import static me.grea.antoine.soda.utils.Collections.intersection;
import static me.grea.antoine.soda.utils.Collections.set;

/**
 *
 * @author antoine
 */
public class CompetingAction extends Defect {

    public Edge competing;
    public Action challenger;

    public CompetingAction(Edge competing, Action challenger, Problem problem) {
        super(problem);
        this.competing = competing;
        this.challenger = challenger;
    }

    @Override
    public void fix() {
        Action target = problem.plan.getEdgeTarget(competing);

        if (problem.plan.containsVertex(challenger) && problem.plan.containsVertex(target) && 
                !problem.plan.reachable(target, challenger, set(competing))) {
            problem.plan.removeEdge(competing);
            problem.plan.addEdge(challenger, target);
        }
    }

    public static Set<CompetingAction> find(Problem problem) {
        Set<CompetingAction> result = new HashSet<>();
        for (Edge edge : problem.plan.edgeSet()) {
            for (Action action : problem.plan.vertexSet()) {
                Action source = problem.plan.getEdgeSource(edge);
                Action target = problem.plan.getEdgeTarget(edge);
                if (action != source && action != target) {
                    CompetingAction competingAction = is(edge, action, source, target, problem);
                    if (competingAction != null) {
                        result.add(competingAction);
                    }
                }
            }
        }
        return result;
    }

    public static CompetingAction is(Edge connected, Action source, Action target, Action challenger, Problem problem) {
        Set<Integer> competing = intersection(connected.labels, challenger.effects);
        if (!competing.isEmpty()
                && usefullness(source, problem) < usefullness(challenger, problem)) {
            return new CompetingAction(connected, challenger, problem);
        }
        return null;
    }

    @Override
    public String toString() {
        return "Action " + challenger + " is a good competitor to replace " + competing;
    }

}
