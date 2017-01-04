/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.genn.color.graph;

/**
 *
 * @author antoine
 */
public interface Edge<V> {
    public V source();
    public V target();
    public int weight();

    @Override
    public boolean equals(Object obj);

    @Override
    public int hashCode();

    @Override
    public String toString();
    
}
