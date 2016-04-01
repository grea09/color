/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.lollipop.type.flaw;

import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import me.grea.antoine.lollipop.type.Action;
import me.grea.antoine.lollipop.type.Edge;
import me.grea.antoine.lollipop.type.Problem;
import static me.grea.antoine.utils.Collections.queue;

/**
 *
 * @author antoine
 */
public class Alternative extends Flaw<Alternative> {

    public Action provider;

    private Alternative(Problem problem) {
        super(problem);
    }

    public Alternative(int fluent, Action provider, Action needer, Problem problem) {
        super(fluent, needer, problem);
        this.provider = provider;
    }

    @Override
    public Deque<Resolver> resolvers() {
        return queue(new Resolver(provider, needer, fluent, true));
    }

//    public static Set<Alternative> find(Problem problem) {
//        Set<Alternative> alternatives = new HashSet<>();
//        Map<Integer, Deque<Resolver>> competitors = new HashMap<>();//cache.get(problem);
////        if (competitors == null) {
////            competitors = new HashMap<>();
////            cache.put(problem, competitors);
////        }
//        for (Edge edge : problem.plan.edgeSet()) {
//            Action provider = problem.plan.getEdgeSource(edge);
//            Action needer = problem.plan.getEdgeTarget(edge);
//            for (Integer label : edge.labels) {
//                Deque<Resolver> resolvers;
//                if (competitors.containsKey(label)) {
//                    resolvers = competitors.get(label);
//                } else {
//                    resolvers = new SubGoal(label, needer, problem).resolvers();
//                    competitors.put(label, resolvers);
//                }
//                for (Resolver resolver : resolvers) {
//                    if (Usefullness.compare(problem).compare(provider, resolver.source) < 0) {
//                        Log.d("⎇ " + resolver.source + " is more usefull for " + edge);
//                        alternatives.add(new Alternative(resolver.fluent, provider, resolver.target, problem));
//                        break;
//                    }
//                }
//            }
//        }
//        return alternatives;
//    }
    public static Set<Alternative> related(Action troubleMaker, Problem problem) {
        return new Alternative(problem).related(troubleMaker);
    }

//    @Override
//    public Set<Alternative> related(Action troubleMaker) { // Forward
//        Set<Alternative> alternatives = new HashSet<>();
//        for (Integer effect : troubleMaker.effects) {
//            List<Action> best = problem.providing.get(effect);
//            for (Action provider : best) {
//                if (problem.ranking.compare(provider, troubleMaker)<0) {
//                    for (Edge edge : problem.plan.outgoingEdgesOf(provider)) {
//                        if (edge.labels.contains(effect)) {
//                            alternatives.add(new Alternative(effect, provider, problem.plan.getEdgeTarget(edge), problem));
//                        }
//                    }
//                }
//            }
//        }
//        return alternatives;
//    }
    @Override
    public Set<Alternative> related(Action troubleMaker) { // Backward
        Set<Alternative> alternatives = new HashSet<>();
        for (Edge edge : problem.plan.incomingEdgesOf(troubleMaker)) {
            Action provider = problem.plan.getEdgeSource(edge);
            for (Integer label : edge.labels) {
                Action pretender = problem.providing.get(label).get(0);
                if (pretender != provider && pretender != troubleMaker) {
                    alternatives.add(new Alternative(label, provider, troubleMaker, problem));
                }
            }
        }
        return alternatives;
    }

    @Override
    public String toString() {
        return provider + " ⎇ " + needer;
    }

}
