/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package psptest.type;

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
import org.jgrapht.alg.DijkstraShortestPath;
import psptest.exception.Failure;

/**
 *
 * @author antoine
 */
public class Problem {

    public Action initial;
    public Action goal;
    public Set<Action> actions; // not including those above
    public DirectedGraph<Action, Edge> plan;

    public Problem(Action initial, Action goal, Set<Action> actions, DirectedGraph<Action, Edge> plan) {
        this.initial = initial;
        this.goal = goal;
        this.actions = actions;
        this.plan = plan;
    }

    public class SubGoal {

        public Action needer;
        public int subgoal;
        private Edge causal;
        private Action provider;

        public SubGoal(Action needer, int subgoal) {
            this.needer = needer;
            this.subgoal = subgoal;
        }

        public void satisfy(Action provider) {
            this.provider = provider;
            plan.addVertex(provider);
            causal = plan.addEdge(provider, needer);
            causal.label = subgoal;
            Log.d("Inserting " + this);
        }

        public void revert() {
            Log.w("Action " + provider + " not suited to achieve " + subgoal + " of " + needer + " ! Reverting");
            plan.removeEdge(causal);
            if (plan.outDegreeOf(provider) == 0) {
                plan.removeVertex(provider);
            }
        }

        @Override
        public String toString() {
            return (provider != null && plan.containsVertex(provider) ? provider : "?")
                    + " =[" + subgoal + "]>" + needer;
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

    public class Threat {

        public Action threat;
        public Edge threatened;
        private Edge demoter;
        private Edge promoter;

        public Threat(Action threat, Edge threatened) {
            this.threat = threat;
            this.threatened = threatened;
        }

        public void demote() throws Failure {
            Log.d("Demoting " + threat + " for the link " + threatened);
            if (plan.getEdgeTarget(threatened) == goal) {
                Log.w("Can't demote after goal step !");
                throw new Failure(this);
            }
            demoter = plan.addEdge(plan.getEdgeTarget(threatened), threat);
            demoter.label = 0;
        }

        public void undemote() {
            plan.removeEdge(demoter);
        }

        public void promote() throws Failure {
            Log.d("Promoting " + threat + " for the link " + threatened);
            if (plan.getEdgeSource(threatened) == initial) {
                Log.w("Can't promote before initial step !");
                throw new Failure(this);
            }
            promoter = plan.addEdge(threat, plan.getEdgeSource(threatened));
            promoter.label = 0;
        }

        public void unpromote() {
            plan.removeEdge(promoter);
        }

        @Override
        public String toString() {
            return threat + " ☠ " + threatened;
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
        return new DijkstraShortestPath<>(plan, source, target).getPathLength() != Double.POSITIVE_INFINITY;
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
