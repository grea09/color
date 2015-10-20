/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.soda.type;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import me.grea.antoine.log.Log;
import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.CycleDetector;
import me.grea.antoine.soda.algorithm.DFS;
import me.grea.antoine.soda.exception.Failure;
import me.grea.antoine.soda.utils.Sets;

/**
 *
 * @author antoine
 */
public class Problem {

    public Action initial;
    public Action goal;
    public Set<Action> actions; // not including those above
    public DirectedGraph<Action, Edge> plan;
    public Map<Flaw, DirectedGraph<Action, Edge>> partialSolutions = new HashMap<>();
    public Set<Action> quarantine = new HashSet<>();

    public Problem(Action initial, Action goal, Set<Action> actions, DirectedGraph<Action, Edge> plan) {
        this.initial = initial;
        this.goal = goal;
        this.actions = actions;
        this.plan = plan;
    }

    public Problem(Problem other) {
        this.initial = other.initial;
        this.goal = other.goal;
        this.actions = other.actions;
        this.plan = other.plan;
    }

    public class Flaw {

        public int fluent;
        public Action needer;

    }

    public class Loop extends Flaw {

        public Edge looping;

        public Loop(Action needer, int subgoal, Edge looping) {
            this.needer = needer;
            this.fluent = subgoal;
            this.looping = looping;
        }

        @Override
        public String toString() {
            return "↺ " + looping;
        }
    }

    public class SubGoal extends Flaw {

        private Edge causal;
        private Action provider;

        public SubGoal(Action needer, int subgoal) {
            this.needer = needer;
            this.fluent = subgoal;
        }

        public void satisfy(Action provider) throws Failure {
            this.provider = provider;
            plan.addVertex(provider);
            causal = plan.addEdge(provider, needer);
            if (causal == null) {
                Log.w("Edge is already present ! Probably a loop in the problem space");
                Edge edge = plan.getEdge(provider, needer);
//                if ((Integer) edge.label == fluent) {
//                    Log.w(edge + "is symptomatic of a loop : quarantine engaged");
                    throw new Failure(Problem.this, new Loop(needer, fluent, edge));
//                } else {
//                    throw new Failure(Problem.this, this);
//                }
            }
            causal.label = fluent;
            Log.i("Inserting " + this);
        }

        public void revert() {
//            Log.w("Action " + provider + " not suited to achieve " + fluent + " of " + needer + " ! Reverting");
            plan.removeEdge(causal);
            if (plan.outDegreeOf(provider) == 0) {
                plan.removeVertex(provider);
            }
        }

        @Override
        public String toString() {
            return (provider != null && plan.containsVertex(provider) ? provider : "?")
                    + " =(" + fluent + ")> " + needer;
        }

    }

    public Set<SubGoal> suboals() {
        Map<Integer, SubGoal> subgoals = new HashMap<>();
        Deque<Action> open = new ArrayDeque<>(Arrays.asList(goal));
        while (!open.isEmpty()) {
            Action current = open.pollLast();
//            for (int effect : current.effects) {
//                subgoals.remove(effect);
//            }
            for (int precondition : current.preconditions) {
//                if (!problem.initial.effects.contains(precondition)) {
                subgoals.put(precondition, new SubGoal(current, precondition));
//                }
            }
            for (Edge edge : plan.incomingEdgesOf(current)) {
                subgoals.remove(edge.label);
                open.addFirst(plan.getEdgeSource(edge));
            }
        }
        return new HashSet<>(subgoals.values());
    }

    public SubGoal findSubGoal() {
        Deque<Action> open = new ArrayDeque<>(Arrays.asList(goal));
        while (!open.isEmpty()) {
            Action current = open.pollLast();
            for (int precondition : current.preconditions) {
                boolean satisfied = false;
                for (Edge edge : plan.incomingEdgesOf(current)) {
                    if ((Integer) edge.label == precondition) {
                        satisfied = true;
                    }
                    open.addFirst(plan.getEdgeSource(edge));
                }
                if (!satisfied) {
                    return new SubGoal(current, precondition);
                }
            }
        }
        return null;
    }

    public class Threat extends Flaw {

        public Action breaker;
        public Edge threatened;
        private Edge demoter;
        private Edge promoter;

        public Threat(Action threat, Edge threatened) {
            this.breaker = threat;
            this.threatened = threatened;
            fluent = (Integer) threatened.label;
            needer = plan.getEdgeTarget(threatened);
        }

        public void demote() throws Failure {
            Log.i("Demoting " + breaker + " for the link " + threatened);
            if (plan.getEdgeTarget(threatened) == goal) {
                Log.w("Can't demote after goal step !");
                throw new Failure(Problem.this, this);
            }
            demoter = plan.addEdge(plan.getEdgeTarget(threatened), breaker);
            demoter.label = 0;
        }

        public void undemote() {
            plan.removeEdge(demoter);
        }

        public void promote() throws Failure {
            Log.i("Promoting " + breaker + " for the link " + threatened);
            if (plan.getEdgeSource(threatened) == initial) {
                Log.w("Can't promote before initial step !");
                throw new Failure(Problem.this, this);
            }
            promoter = plan.addEdge(breaker, plan.getEdgeSource(threatened));
            promoter.label = 0;
        }

        public void unpromote() {
            plan.removeEdge(promoter);
        }

        @Override
        public String toString() {
            return breaker + " ☠ " + threatened;
        }

    }

    public Set<Threat> threats() {
        Set<Threat> threats = new HashSet<>();
        for (Action candidate : plan.vertexSet()) {
            for (int effect : candidate.effects) {
                for (Edge oposite : plan.edgeSet()) {
                    if ((Integer) oposite.label == -effect) { //NEVER have a 0 effect
                        Action source = plan.getEdgeSource(oposite);
                        Action target = plan.getEdgeTarget(oposite);
//                        if (!plan.containsEdge(candidate, source)
//                                && !plan.containsEdge(target, candidate)) {
                        if (!pathExists(candidate, source) && !pathExists(target, candidate)) {
                            threats.add(new Threat(candidate, oposite));
                        }
//                        }
                    }
                }
            }
        }
        return threats;
    }

    public Threat findThreat() {
        for (Action candidate : plan.vertexSet()) {
            for (int effect : candidate.effects) {
                for (Edge oposite : plan.edgeSet()) {
                    if ((Integer) oposite.label == -effect) { //NEVER have a 0 effect
                        Action source = plan.getEdgeSource(oposite);
                        Action target = plan.getEdgeTarget(oposite);
//                        if (!plan.containsEdge(candidate, source)
//                                && !plan.containsEdge(target, candidate)) {
                        if (!pathExists(candidate, source) && !pathExists(target, candidate)) {
                            return new Threat(candidate, oposite);
                        }
//                        }
                    }
                }
            }
        }

        return null;
    }

    public boolean consistent() {
        return !(new CycleDetector<>(plan).detectCycles());
    }

    public boolean pathExists(Action source, Action target) {
//        return new DijkstraShortestPath<>(plan, source, target).getPathLength() != Double.POSITIVE_INFINITY;
        return DFS.reachable(plan, source, target);
    }

    public long violation() {
        return plan.vertexSet().stream().filter((action) -> (action.fake)).count();
    }

    @Override
    public String toString() {
        return "Problem {" + "\n\tinitial:" + initial.effects + " => goal:" + ((Goal) goal).toString() + "\n\tactions:" + actions + "\n\tplan:" + planToString() + "}";
    }

    public String planToString() {
//        return plan.edgeSet().toString();
        String result = "";
        for (Edge edge : plan.edgeSet()) {
            result += edge + "\n\t";
        }
        return result;
    }

}
