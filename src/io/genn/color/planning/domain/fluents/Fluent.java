/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.genn.color.planning.domain.fluents;

import io.genn.color.planning.domain.fluents.FluentControl;
import java.util.Map;

/**
 *
 * @author antoine
 */
public interface Fluent<F extends Fluent<F, E>, E> {

	public boolean unifies(F lesser);

	public Map<E, E> unify(F lesser); // returns variable bindings

	public F instanciate(Map<E, E> unify); // returns an instance using unify

	public boolean contradicts(F counter); // imply that it doesn't unifies

	public boolean coherent(); // imply that it doesn't unifies

	public F negate(); //must contradicts 

	public boolean negative();

	public FluentControl<F, E> control();
}
