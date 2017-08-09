/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.genn.color.planning.domain;

import java.util.List;

/**
 *
 * @author antoine
 */
public interface FluentControl<F extends Fluent<F,E> , E> {
	public E instanciate(Action<F, E> lifted, List<E> parameters);
}
