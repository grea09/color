/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package yocto.plannification;

import java.util.Set;

/**
 *
 * @author antoine
 */
public interface Action extends Goal, Comparable<Action> {

    public Set<Statement> getEffects();

}
