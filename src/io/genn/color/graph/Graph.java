/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.genn.color.graph;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import static me.grea.antoine.utils.collection.Collections.*;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.HashSetValuedHashMap;

/**
 * Custom dirrected graph
 *
 * @author antoine
 * @param <V> Vertex type
 * @param <E> Edge type
 */
public class Graph<V, E extends Edge<V>> {

    private final Map<V, DirectedEdges<V, E>> graph;
    private final Class<?> edgeClass;
    private final MultiValuedMap<V, V> reachability;
    public final boolean acyclic;

    public Graph(Class<?> edgeClass) {
        this(edgeClass, true);
    }

    public Graph(Class<?> edgeClass, boolean acyclic) {
        this.edgeClass = edgeClass;
        graph = new HashMap<>();
        reachability = new HashSetValuedHashMap();
        this.acyclic = acyclic;
    }

    public Graph(Graph other) {
        this.edgeClass = other.edgeClass;
        graph = new HashMap<>(other.graph);
        reachability = new HashSetValuedHashMap(other.reachability);
        acyclic = other.acyclic;
    }

    private V build(Class<?> vertexClass, String name) {
        try {
            return (V) vertexClass.getConstructor(String.class).newInstance(name);
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new RuntimeException(ex);
        }
    }

    private E build(V source, V target) {
        try {
            return (E) edgeClass.getConstructors()[0].newInstance(source, target);
        } catch (SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new RuntimeException(ex);
        }
    }

    public boolean reachable(V source, V target) //TODO tests
    {
        return source == target || reachability.containsMapping(source, target);
    }

    private void additiveReachabilityUpdate(V source, V target) {
        if (acyclic) {
            reachability.put(source, target);
            for (Map.Entry<V, V> entry : new HashSet<>(reachability.entries())) {
                if (source.equals(entry.getValue())) {
                    reachability.put(entry.getKey(), target);
                }
                if (target.equals(entry.getKey())) {
                    reachability.put(source, entry.getValue());
                }
            }
        }
    }

    private void substractiveReachabilityUpdate(V source, V target) {
        if (acyclic) {
            reachability.removeMapping(source, target);
            for (Map.Entry<V, V> entry : new HashSetValuedHashMap<>(reachability).entries()) {
                if (source.equals(entry.getValue()) && !containsEdge(entry.getKey(), target)) {
                    reachability.removeMapping(entry.getKey(), target);
                }
                if (target.equals(entry.getKey()) && !containsEdge(source, entry.getValue())) {
                    reachability.removeMapping(source, entry.getValue());
                }
            }
        }
    }

    public E addEdge(E edge) {
        if (edge == null || edge.source() == null || edge.target() == null) {
            throw new NullPointerException("Null values not allowed in graph.");
        }
        V source = edge.source();
        V target = edge.target();
        if (containsEdge(source, target)) {
            removeEdge(source, target);
        }
        if (acyclic && (source == target || reachability.containsMapping(target, source))) {
            throw new IllegalStateException("Adding " + edge + " will create a cycle !");
        }
        addVertex(source);
        addVertex(target);
        additiveReachabilityUpdate(source, target);
        graph.get(source).out.add(edge);
        graph.get(target).in.add(edge);
        return edge;
    }

    public E addEdge(V source, V target) {
        E edge = edge(source, target);
        if (edge != null) {
            return edge;
        }
        edge = build(source, target);
        return addEdge(edge);
    }

    public boolean addVertex(V vertex) {
        if (graph.containsKey(vertex)) {
            return false;
        }
        graph.put(vertex, new DirectedEdges<>(vertex));
        return true;
    }

    public boolean containsEdge(V source, V target) {
        return !edges(source, target).isEmpty();
    }

    public boolean containsEdge(E e) {
        return containsEdge(e.source(), e.target());
    }

    public boolean containsVertex(V vertex) {
        return graph.containsKey(vertex);
    }

    public Set<E> edgeSet() {
        return union((Collection<? extends Set<E>>) graph.values());
    }

    public Set<E> edgesOf(V vertex) {
        return graph.get(vertex);
    }

    public boolean removeAllEdges(Collection<? extends E> edges) {
        boolean modified = false;
        for (Set<E> e : graph.values()) {
            modified |= e.removeAll(edges);
        }
        if (modified && acyclic) {
            for (E edge : edges) {
                substractiveReachabilityUpdate(edge.source(), edge.target());
            }
        }
        return modified;
    }

    public boolean removeAllVertices(Collection<? extends V> vertices) {
        boolean modified = false;
        for (V vertex : vertices) {
            modified |= removeVertex(vertex);
        }
        return modified;
    }

    public Set<E> edges(V source, V target) { //FIXME extensive testing required
        Set<E> edges = new HashSet<>();
        Set<E> out = containsVertex(source) ? graph.get(source).out : set();
        Set<E> in = containsVertex(target) ? graph.get(target).in : set();
        for (E edge : union(out, in)) {
            if ((source == null || source.equals(edge.source())) && (target == null || target.equals(edge.target()))) {
                edges.add(edge);
            }
        }
        return edges;
    }

    public E edge(V source, V target) {
        if (!containsVertex(source) || !containsVertex(target)) {
            return null;
        }
        for (E e : intersection(graph.get(source).out, graph.get(target).in)) {
            return e;
        }
        return null;
    }

    public boolean removeEdge(E toRemove) { //FIXME Expensive
        return removeAllEdges(set(toRemove));
    }

    public E removeEdge(V source, V target) { //FIXME VERY Expensive
        E toRemove = edge(source, target);
        removeEdge(toRemove);
        return toRemove;
    }

    public boolean removeVertex(V v) {
        Set<E> related = graph.remove(v);
        if (related == null) {
            return false;
        }
        return removeAllEdges(related);
    }

    public Set<V> vertexSet() {
        return graph.keySet();
    }

    public int inDegreeOf(V vertex) {
        return incomingEdgesOf(vertex).size();
    }

    public Set<E> incomingEdgesOf(V vertex) {
        if (!containsVertex(vertex)) {
            return set();
        }
        return graph.get(vertex).in;
    }

    public int outDegreeOf(V vertex) {
        return outgoingEdgesOf(vertex).size();
    }

    public Set<E> outgoingEdgesOf(V vertex) {
        if (!containsVertex(vertex)) {
            return set();
        }
        return graph.get(vertex).out;
    }

    @Override
    public String toString() {
        String edges = "";
        return "digraph {\n" + edgeSet().stream().map((edge) -> "\t" + edge + "\n").reduce(edges, String::concat) + '}';
    }

}
