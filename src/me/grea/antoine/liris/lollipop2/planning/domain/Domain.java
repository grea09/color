/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.liris.lollipop2.planning.domain;

import java.util.Collection;
import java.util.HashSet;

/**
 *
 * @author antoine
 * @param <F>
 */
public class Domain<F extends Fluent> extends HashSet<Action<F>> {

    public Domain() {
    }

    public Domain(Collection<? extends Action<F>> c) {
        super(c);
    }
    
    @Override
    public String toString() {
        String accumulator = "";
        accumulator = (stream().map((action) -> "\t" + action + "\n").reduce(accumulator, String::concat));
        return "{\n" + accumulator.substring(0,accumulator.length()-1) + "}";
    }
    
}
