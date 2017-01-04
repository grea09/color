/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.genn.color.graph;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import static me.grea.antoine.utils.Collections.union;

/**
 *
 * @author antoine
 * @param <V>
 * @param <E>
 */
public class DirectedEdges<V, E extends Edge<V>> implements Set<E> {

    public final Set<E> out = new HashSet<>();
    public final Set<E> in = new HashSet<>();
    private final V relative;

    public DirectedEdges(V relative) {
        this.relative = relative;
    }

    public DirectedEdges(Collection<E> edges, V relative) {
        this(relative);
        for (E edge : edges) {
            if (relative == edge.source()) {
                out.add(edge);
            } else if (relative == edge.target()) {
                in.add(edge);
            }
        }
    }

    public DirectedEdges(DirectedEdges<V, E> other) {
        this(other.relative);
        out.addAll(other.out);
        in.addAll(other.in);
    }

    public boolean contains(E edge) {
        return in.contains(edge) || out.contains(edge);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.out);
        hash = 97 * hash + Objects.hashCode(this.in);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            if (obj instanceof Collection) {
                return obj.equals(this);
            }
            return false;
        }
        final DirectedEdges<?, ?> other = (DirectedEdges<?, ?>) obj;
        if (!Objects.equals(this.out, other.out)) {
            return false;
        }
        if (!Objects.equals(this.in, other.in)) {
            return false;
        }
        return true;
    }

    private Set<E> all() {
        return union(in, out);
    }

    @Override
    public int size() {
        return all().size();
    }

    @Override
    public boolean isEmpty() {
        return in.isEmpty() && out.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return in.contains(o) || out.contains(o);
    }

    @Override
    public Iterator<E> iterator() {
        return union(in, out).iterator();
    }

    @Override
    public Object[] toArray() {
        return all().toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return all().toArray(a);
    }

    @Override
    public boolean add(E edge) {
        boolean modified = false;
        if (relative == edge.source()) {
            modified |= out.add(edge);
        } else if (relative == edge.target()) {
            modified |= in.add(edge);
        }
        return modified;
    }

    @Override
    public boolean remove(Object o) {
        boolean modified = false;
        modified |= in.remove(o);
        modified |= out.remove(o);
        return modified;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return all().containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        boolean modified = false;
        for (E e : c) {
            modified |= add(e);
        }
        return modified;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        boolean modified = false;
        modified |= in.retainAll(c);
        modified |= out.retainAll(c);
        return modified;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean modified = false;
        modified |= in.removeAll(c);
        modified |= out.removeAll(c);
        return modified;
    }

    @Override
    public void clear() {
        in.clear();
        out.clear();
    }

}
