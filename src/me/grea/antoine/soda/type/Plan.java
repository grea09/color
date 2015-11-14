/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.soda.type;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;
import org.jgrapht.graph.DefaultDirectedGraph;

/**
 *
 * @author antoine
 */
public class Plan extends DefaultDirectedGraph<Action, Edge>{

    public Plan() {
        super(Edge.class);
    }
    
    public boolean kReachable(Action source, Action target, int k) {
        if (source == target) {
            return true;
        }
        Set<Action> visited = new HashSet<>();
        Deque<Action> stack = new ArrayDeque<>();
        stack.push(source);
        while (!stack.isEmpty()) {
            Action current = stack.pop();
            for (Edge edge : outgoingEdgesOf(current)) {
                Action frontier = getEdgeTarget(edge);
                if (frontier == target && --k <= 0) {
                    return true;
                }
                if (visited.contains(frontier)) {
                    continue;
                }
                visited.add(frontier);
                stack.add(frontier);
            }
        }
        return false;
    }

    public boolean reachable(Action source, Action target) {
        return kReachable(source, target, 1);
    }
    
}
