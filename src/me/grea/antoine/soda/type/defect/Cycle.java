/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.soda.type.defect;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import me.grea.antoine.soda.type.Action;
import me.grea.antoine.soda.type.Edge;
import me.grea.antoine.soda.type.Problem;
import org.jgrapht.alg.StrongConnectivityInspector;
import static me.grea.antoine.soda.utils.Collections.*;

/**
 *
 * @author antoine
 */
public class Cycle extends Defect {

    Set<Action> cycling;

    public Cycle(Set<Action> cycling, Problem problem) {
        super(problem);
        this.cycling = cycling;
    }

    @Override
    public void fix() {
        for (Action action : cycling) {
            for (Edge edge : problem.plan.edgesOf(action)) {
                if (cycling.contains(problem.plan.getEdgeTarget(edge))) {

                    problem.plan.removeEdge(edge);
                    return;
                }
            }
        } // already solved
    }

    public static Set<Cycle> find(Problem problem) {
        List<Set<Action>> components = new StrongConnectivityInspector<>(problem.plan).stronglyConnectedSets();

        // A vertex participates in a cycle if either of the following is
        // true:  (a) it is in a component whose size is greater than 1
        // or (b) it is a self-loop
        Set<Cycle> cycles = set();
        for (Set<Action> component : components) {
            if (component.size() > 1) {
                // cycle
                cycles.add(new Cycle(component, problem));
            } else {
                Action action = component.iterator().next();
                if (problem.plan.containsEdge(action, action)) {
                    // self-loop
                    cycles.add(new Cycle(component, problem));
                }
            }
        }
        return cycles;
    }

    @Override
    public String toString() {
        return "Actions " + cycling + " forms a cycle";
    }

}
