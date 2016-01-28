/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.lollipop.type;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.AbstractBaseGraph;
import org.jgrapht.graph.ClassBasedEdgeFactory;

/**
 *
 * @author antoine
 */
public class Plan extends AbstractBaseGraph<Action, Edge> implements DirectedGraph<Action, Edge> {

    private final Map<Action, Integer> outDegree = new HashMap<Action, Integer>() {
        {
            vertexSet().stream().forEach((action) -> {
                put(action, outDegreeOf(action));
            });
        }
    };
    private final Map<Action, Integer> inDegree = new HashMap<Action, Integer>() {
        {
            vertexSet().stream().forEach((action) -> {
                put(action, inDegreeOf(action));
            });
        }
    };

    private int violation = 0;

    public Plan() {
        super(new ClassBasedEdgeFactory<>(Edge.class), false, true);
    }

    public Plan(Plan other) {
        this();
        for (Action action : other.vertexSet()) {
            addVertex(action);
        }
        for (Edge edge : other.edgeSet()) {
            addEdge(other.getEdgeSource(edge), other.getEdgeTarget(edge), edge);
        }
    }

    public boolean reachable(Action source, Action target, int k, Set<Edge> forbiden) {
        if (source == target) {
            return true;
        }
        if (!containsVertex(source) || !containsVertex(target)) {
            return false;
        }
        if (forbiden == null) {
            forbiden = new HashSet<>();
        }
        Set<Action> visited = new HashSet<>();
        Deque<Action> stack = new ArrayDeque<>();
        stack.push(source);
        while (!stack.isEmpty()) {
            Action current = stack.pop();
            for (Edge edge : outgoingEdgesOf(current)) {
                if (forbiden.contains(edge)) {
                    continue;
                }
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

    public boolean reachable(Action source, Action target, int k) {
        return reachable(source, target, k, null);
    }

    public boolean reachable(Action source, Action target, Set<Edge> forbiden) {
        return reachable(source, target, 1, forbiden);
    }

    public boolean reachable(Action source, Action target) {
        return reachable(source, target, 1, null);
    }

    @Override
    public int outDegreeOf(Action vertex) {
        return outDegree.get(vertex);
    }

    @Override
    public int inDegreeOf(Action vertex) {
        return inDegree.get(vertex);
    }

    @Override
    public int degreeOf(Action vertex) {
        return outDegreeOf(vertex) + inDegreeOf(vertex);
    }

    @Override
    public boolean addVertex(Action v) {
        if (super.addVertex(v)) {
            outDegree.put(v, 0);
            inDegree.put(v, 0);
            violation += v.fake ? 1 : 0;
            return true;
        }
        return false;
    }

    @Override
    public boolean addEdge(Action sourceVertex, Action targetVertex, Edge e) {
        if (super.addEdge(sourceVertex, targetVertex, e)) {
            outDegree.put(sourceVertex, outDegreeOf(sourceVertex) + 1);
            inDegree.put(targetVertex, outDegreeOf(targetVertex) + 1);
            return true;
        }
        return false;
    }

    @Override
    public Edge addEdge(Action sourceVertex, Action targetVertex) {
        Edge edge;
        if ((edge = super.addEdge(sourceVertex, targetVertex)) != null) {
            outDegree.put(sourceVertex, outDegreeOf(sourceVertex) + 1);
            inDegree.put(targetVertex, inDegreeOf(targetVertex) + 1);
        }
        return edge;
    }

    @Override
    public boolean removeVertex(Action v) {
        if (super.removeVertex(v)) {
            outDegree.remove(v);
            inDegree.remove(v);
            violation -= v.fake ? 1 : 0;
            return true;
        }
        return false;
    }

    @Override
    public boolean removeEdge(Edge e) {
        if (super.removeEdge(e)) {
            Action source = getEdgeSource(e);
            Action target = getEdgeTarget(e);
            outDegree.put(source, outDegreeOf(source) - 1);
            inDegree.put(target, inDegreeOf(target) - 1);
            return true;
        }
        return false;
    }

    @Override
    public Edge removeEdge(Action sourceVertex, Action targetVertex) {
        Edge edge;
        if ((edge = super.removeEdge(sourceVertex, targetVertex)) != null) {
            outDegree.put(sourceVertex, outDegreeOf(sourceVertex) - 1);
            inDegree.put(targetVertex, inDegreeOf(targetVertex) - 1);
        }
        return edge;
    }

    public long violation() {
        return violation;
    }

}
