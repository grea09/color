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
import static me.grea.antoine.soda.utils.Collections.intersection;

/**
 *
 * @author antoine
 */
public class CompetingLink extends Defect {

    public Edge greater;
    public Edge lesser;
    public Set<Integer> competing;

    public CompetingLink(Edge greater, Edge lesser, Set<Integer> competing, Problem problem) {
        super(problem);
        this.greater = greater;
        this.lesser = lesser;
        this.competing = competing;
    }

    @Override
    public void fix() {
        lesser.labels.removeAll(competing);
        if (lesser.labels.isEmpty()) {
            problem.plan.removeEdge(lesser);
        }
    }

    public static Set<CompetingLink> find(Problem problem) {
        Set<CompetingLink> result = new HashSet<>();
        Set<Edge> concurents = new HashSet<>();
        for (Edge edge : problem.plan.edgeSet()) {
            for (Edge concurent : problem.plan.incomingEdgesOf(problem.plan.getEdgeTarget(edge))) {
                if (concurent != edge && !concurents.contains(edge) && !concurents.contains(concurent)) {
                    CompetingLink competingLink = is(edge, concurent, problem);
                    if (competingLink != null) {
                        concurents.add(edge);
                        concurents.add(concurent); //FIXME check if this doesn't cause duplicates
                        result.add(competingLink);
                    }
                }
            }
        }
        return result;
    }

    public static CompetingLink is(Edge pretender, Edge challenger, Problem problem) {
        Set<Integer> competing = intersection(pretender.labels, challenger.labels);
        if (problem.plan.getEdgeTarget(pretender) == problem.plan.getEdgeTarget(challenger)
                && !competing.isEmpty()) {
            Action pretenderSource = problem.plan.getEdgeSource(pretender);
            Action challengerSource = problem.plan.getEdgeSource(challenger);
            if (usefullness(pretenderSource, problem) >= usefullness(challengerSource, problem)) {
                return new CompetingLink(pretender, challenger, competing, problem);
            } else {
                return new CompetingLink(challenger, pretender, competing, problem);
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "The edge " + greater + " should erase " + lesser + " because they both provide " + competing;
    }

}
