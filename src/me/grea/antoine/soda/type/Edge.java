/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.soda.type;

import java.util.HashSet;
import java.util.Set;
import org.jgrapht.graph.DefaultEdge;

/**
 *
 * @author antoine
 */
public class Edge extends DefaultEdge {

    public Set<Integer> labels = new HashSet<>();

    @Override
    public String toString() {
        return getSource() + " -" + (labels.isEmpty() ? "☠" : labels) + "> " + getTarget();
    }

}
