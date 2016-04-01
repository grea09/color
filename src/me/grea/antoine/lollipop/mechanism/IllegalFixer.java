/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.lollipop.mechanism;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import me.grea.antoine.lollipop.type.Action;
import me.grea.antoine.lollipop.type.Edge;
import me.grea.antoine.lollipop.type.Problem;
import static me.grea.antoine.utils.Collections.difference;
import static me.grea.antoine.utils.Collections.intersection;
import static me.grea.antoine.utils.Collections.set;
import static me.grea.antoine.utils.Collections.union;
import me.grea.antoine.utils.Log;
import org.jgrapht.alg.StrongConnectivityInspector;
import static me.grea.antoine.utils.Collections.union;

/**
 *
 * @author antoine
 */
public class IllegalFixer {

    public static void clean(Problem problem) {
        for (Action action : union(problem.domain, problem.plan.vertexSet())) {
            fixAction(action, problem);
        }

        for (Edge edge : new HashSet<>(problem.plan.edgeSet())) {
            fixEdge(edge, problem);
        }

//        fixCycles(problem);
    }

    private static Set<Integer> lies(Edge edge, Action source, Action target) {
        return difference(edge.labels, intersection(source.effects, target.preconditions));
    }

    private static Set<Integer> saviours(Edge edge, Action source, Action target) {
        return difference(intersection(source.effects, target.preconditions), edge.labels);
    }

    private static Set<Integer> toxicEffects(Action action) {
        Set<Integer> effects = intersection(action.preconditions, action.effects);
        if (effects.isEmpty()) {
            return set();
        }
        return effects;
    }

    private static Set<Integer> inconsistentEffects(Action action) {
        Set<Integer> effects = new HashSet<>();
        for (int fluent : action.effects) {
            if (action.effects.contains(-fluent)) {
                effects.add(fluent);
            }
        }
        return effects;
    }

    private static Set<Integer> inconsistentPrecondition(Action action) {
        Set<Integer> preconditions = new HashSet<>();
        for (int fluent : action.effects) {
            if (action.preconditions.contains(-fluent)) {
                preconditions.add(fluent);
            }
        }
        return preconditions;
    }

    private static void fixAction(Action action, Problem problem) { //☣
        if (action == problem.goal) { // The goal can't and shouldn't be toxic
            return;
        }
        if (action.effects.isEmpty()) {
            Log.d("◯\t" + action + " is useless ! Getting rid of it.");
            problem.plan.removeVertex(action);
            problem.domain.remove(action);
            return;
        }

        Set<Integer> toxic = toxicEffects(action);
        if (!toxic.isEmpty()) {
            if (action.effects.containsAll(toxic)) {
                Log.d("☣\t" + action + " is entirely toxic ! Getting rid of it.");
                problem.plan.removeVertex(action);
                problem.domain.remove(action);
                return;
            }
            if (action.effects.removeAll(toxic)) {
                Log.d("☣\t" + action + " had the toxic fluent(s)" + toxic);
            }
            for (Edge edge : problem.plan.outgoingEdgesOf(action)) {
                if (toxic.containsAll(edge.labels)) {
                    Log.d("☣\t" + edge + " is entirely infected ! Getting rid of it.");
                    problem.plan.removeEdge(edge);
                    continue;
                }
                if (edge.labels.removeAll(toxic)) {
                    Log.d("☣\t" + edge + " is infected by toxic fluent(s) " + toxic);
                }
            }
        }

        Set<Integer> inconsistents = inconsistentPrecondition(action);
        if (action.preconditions.removeAll(inconsistents)) {
            Log.d("⊭\t" + action + " has inconsistent preconditions " + inconsistents);
        }

        inconsistents = inconsistentEffects(action);
        if (!inconsistents.isEmpty()) {
            if (action.effects.containsAll(inconsistents)) {
                Log.d("⊭\t" + action + " is entirely incosistent ! Getting rid of it.");
                problem.plan.removeVertex(action);
                problem.domain.remove(action);
                return;
            }
            if (action.effects.removeAll(inconsistents)) {
                Log.d("⊭\t" + action + " has inconsistent effects)" + toxic);
            }
        }
    }
    
    public static boolean fixAction(Action action) { //☣
        
        if (action.effects.isEmpty()) {
            Log.d("◯\t" + action + " is useless ! Getting rid of it.");
            return false;
        }

        Set<Integer> toxic = toxicEffects(action);
        if (!toxic.isEmpty()) {
            if (action.effects.containsAll(toxic)) {
                Log.d("☣\t" + action + " is entirely toxic ! Getting rid of it.");
                return false;
            }
            if (action.effects.removeAll(toxic)) {
                Log.d("☣\t" + action + " had the toxic fluent(s)" + toxic);
            }
        }

        Set<Integer> inconsistents = inconsistentPrecondition(action);
        if (action.preconditions.removeAll(inconsistents)) {
            Log.d("⊭\t" + action + " has inconsistent preconditions " + inconsistents);
        }

        inconsistents = inconsistentEffects(action);
        if (!inconsistents.isEmpty()) {
            if (action.effects.containsAll(inconsistents)) {
                Log.d("⊭\t" + action + " is entirely incosistent ! Getting rid of it.");
                return false;
            }
            if (action.effects.removeAll(inconsistents)) {
                Log.d("⊭\t" + action + " has inconsistent effects)" + toxic);
            }
        }
        return true;
    }

    private static void fixEdge(Edge edge, Problem problem) {//☍
        if (edge.labels.isEmpty()) {
            return;
        }
        Action source = problem.plan.getEdgeSource(edge);
        Action target = problem.plan.getEdgeTarget(edge);
        Set<Integer> lies = lies(edge, source, target);
        if (edge.labels.removeAll(lies)) {
            Log.d("☍\t" + edge + " was lying about " + lies);
            Set<Integer> saviours = saviours(edge, source, target);
            if (edge.labels.addAll(saviours)) {
                Log.d("♘\t" + edge + " was saved by " + saviours);
            }
        }
        if (edge.labels.isEmpty()) {
            Log.d("☍\t" + edge + " was lying too much ! Getting rid of it.");
            problem.plan.removeEdge(edge);
            return;
        }
        if (problem.plan.reachable(target, source)) {
            Log.d("⟳\t" + edge + " causes a cycle ! Getting rid of it.");
            problem.plan.removeEdge(edge);
        }
    }

//    private static void fixCycles(Problem problem) {
//        List<Set<Action>> components = new StrongConnectivityInspector<>(problem.plan).stronglyConnectedSets();
//
//        // A vertex participates in a cycle if either of the following is
//        // true:  (a) it is in a component whose size is greater than 1
//        // or (b) it is a self-loop
//        for (Set<Action> component : components) {
//            if (component.size() > 1) {
//                Action lessUsefull = Collections.min(component, Usefullness.compare(problem));
//                for (Edge edge : problem.plan.outgoingEdgesOf(lessUsefull)) {
//                    if (component.contains(problem.plan.getEdgeTarget(edge))) {
//                        Log.d("⟳\tGoing in circle in " + component + ". Breaking at " + edge);
//                        problem.plan.removeEdge(edge);
//                        break;
//                    }
//                }
//            } else {
//                Action action = component.iterator().next();
//                if (problem.plan.removeEdge(action, action) != null) {
//                    Log.d("⟳\t" + action + " loops on itself !");
//                }
//            }
//        }
//    }
}
