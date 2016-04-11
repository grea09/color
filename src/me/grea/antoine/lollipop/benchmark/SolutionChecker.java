/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.lollipop.benchmark;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import me.grea.antoine.lollipop.type.Action;
import me.grea.antoine.lollipop.type.Edge;
import me.grea.antoine.lollipop.type.Plan;
import me.grea.antoine.lollipop.type.Problem;
import me.grea.antoine.lollipop.type.flaw.ClassicalThreat;
import static me.grea.antoine.utils.Collections.permute;
import static me.grea.antoine.utils.Collections.set;
import me.grea.antoine.utils.Log;

/**
 *
 * @author antoine
 */
public class SolutionChecker {

    public static void display(Problem problem) {
        if (!SolutionChecker.check(problem)) {
            Log.f("Solution invalid :{\n\t" + problem + "}");
        }
    }

    public static boolean check(Problem problem) {
        if (problem.plan.edgeSet().isEmpty()) {
            Log.w("Empty plan !");
            return false;
        }
        
        if (!problem.plan.cycles().isEmpty()) {
            Log.w("Cycles " + problem.plan.cycles());
            return false;
        }

        Set<Integer> state = new HashSet<>();
        Set<Action> open = set(problem.initial);
        for (Action action : problem.plan.vertexSet()) {
            Set<ClassicalThreat> threats = ClassicalThreat.related(action, problem);
            if (!threats.isEmpty()) {
                Log.w(action + " is threatening : " + threats);
                return false;
            }
            if (problem.plan.inDegreeOf(action) == 0 && problem.plan.outDegreeOf(action) > 0) {
                if (!action.preconditions.isEmpty()) {
                    Log.w(action + " isn't satisfied at all");
                    return false;
                }
                open.add(action);
            }
        }

        Set<Action> closed = new HashSet<>();
        while (!open.isEmpty()) {
            Set<Integer> additive = new HashSet<>(state);
            for (List<Action> permutation : possiblePermutation(open, problem.plan)) {
                additive = new HashSet<>(state);
                permutee:
                for (Action action : permutation) {
                    if (!state.containsAll(action.preconditions)) {
                        for (Edge edge : problem.plan.incomingEdgesOf(action)) {
                            if (!state.containsAll(edge.labels)) {
//                                open.remove(action);
//                                open.add(problem.plan.getEdgeSource(edge));
                                continue permutee;
                            }
                        }

                        Log.w(action + " can't be applied on state " + state);
                        return false;
                    }
                    for (int effect : action.effects) {
                        additive.add(effect);
                        additive.remove(-effect);
                    }
                }
            }
            state = additive;
            HashSet<Action> next = new HashSet<>();
            closed.addAll(open);
            for (Action action : open) {
                choice:
                for (Edge edge : problem.plan.outgoingEdgesOf(action)) {
                    for (Edge other : problem.plan.incomingEdgesOf(problem.plan.getEdgeTarget(edge))) {
                        if (!closed.contains(problem.plan.getEdgeSource(other))) {
                            Log.d("Can't select " + edge + " because the action " + problem.plan.getEdgeSource(other) + " haven't been selected.");
//                            closed.remove(action);
                            next.add(problem.plan.getEdgeSource(other));
                            continue choice;
                        }
                    }
                    next.add(problem.plan.getEdgeTarget(edge));
                }
            }
            open = next;
        }
        Log.d("Explored for solution validity : " + closed);
        return closed.contains(problem.goal);
    }

    private static Set<List<Action>> possiblePermutation(Set<Action> set, Plan plan) {
        Set<List<Action>> permutations = new HashSet<>();
        if (set.size() == 1) {
            permutations.add(new ArrayList<>(set));
            return permutations;
        }
        choice:
        for (Action step : set) {
            if (plan.containsVertex(step)) {
                for (Edge edge : plan.incomingEdgesOf(step)) {
                    if (set.contains(plan.getEdgeSource(edge))) {
                        continue choice;
                    }
                }
            }

            HashSet<Action> remaining = new HashSet<>(set);
            remaining.remove(step);
            for (List<Action> permutation : permute(remaining)) {
                permutation.add(0, step);
                permutations.add(permutation);
            }
        }
        return permutations;
    }
}
