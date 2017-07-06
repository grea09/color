/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.genn.color.planning.domain;

/**
 *
 * @author antoine
 */
public interface Fluent<F extends Fluent> {
    public boolean unifies(F lesser);
    public boolean contradicts(F counter); // imply that it doesn't unifies
    public F negate(); //must contradicts 
    public boolean negative();
}
