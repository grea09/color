/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.genn.color.planning.algorithm;

import io.genn.color.planning.domain.Action;
import io.genn.color.planning.domain.State;
import io.genn.color.planning.domain.fluents.Fluent;
import io.genn.color.planning.plan.CausalLink;
import io.genn.color.planning.plan.Plan;
import java.util.Collection;
import java.util.Set;

/**
 *
 * @author antoine
 */
public interface Resolver<F extends Fluent<F, ?>> {

	public boolean appliable(Plan plan);
	public void apply(Plan plan);
	public void revert();
	public Collection<Change> changes();
	public State<F> provides();
	
}
