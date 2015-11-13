/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.soda.utils;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;
import org.jgrapht.DirectedGraph;

/**
 *
 * @author antoine
 */
public class DFS {

    public static <V, E> boolean reachable(DirectedGraph<V, E> plan, V source, V target) {
        return kReachable(plan, source, target, 1);
    }
    
    public static <V, E> boolean kReachable(DirectedGraph<V, E> plan, V source, V target, int k) {
        if (source == target) {
            return true;
        }
        Set<V> visited = new HashSet<>();
        Deque<V> stack = new ArrayDeque<>();
        stack.push(source);
        while (!stack.isEmpty()) {
            V current = stack.pop();
            for (E edge : plan.outgoingEdgesOf(current)) {
                V frontier = plan.getEdgeTarget(edge);
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

}
