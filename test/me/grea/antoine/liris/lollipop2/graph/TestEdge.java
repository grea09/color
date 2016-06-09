/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.liris.lollipop2.graph;

import java.util.Objects;

/**
 *
 * @author antoine
 */
public class TestEdge implements Edge<String> {

    private final String source;
    private final String target;

    public TestEdge(String source, String target) {
        this.source = source;
        this.target = target;
    }

    @Override
    public String source() {
        return source;
    }

    @Override
    public String target() {
        return target;
    }

    @Override
    public int weight() {
        return 0;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + Objects.hashCode(this.source);
        hash = 29 * hash + Objects.hashCode(this.target);
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
            return false;
        }
        final TestEdge other = (TestEdge) obj;
        if (!Objects.equals(this.source, other.source)) {
            return false;
        }
        if (!Objects.equals(this.target, other.target)) {
            return false;
        }
        return true;
    }

}
