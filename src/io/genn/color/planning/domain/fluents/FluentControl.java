/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.genn.color.planning.domain.fluents;

import io.genn.color.planning.domain.Action;
import io.genn.world.data.Entity;
import java.util.List;
import java.util.Map;

/**
 *
 * @author antoine
 */
public interface FluentControl<F extends Fluent<F, E>, E> {

	public E instanciate(Action<F, E> lifted, List<E> parameters);

	public E instanciate(E entity, Map<E, E> unify);

	public boolean discard(F fluent);
}
