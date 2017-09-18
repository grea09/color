/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.genn.color.planning.algorithm;

import io.genn.color.planning.domain.State;
import io.genn.color.planning.domain.fluents.Fluent;
import io.genn.color.planning.plan.Plan;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import static me.grea.antoine.utils.collection.Collections.list;
import java.util.List;

/**
 *
 * @author antoine
 */
public class CompositeResolver<F extends Fluent<F, ?>> extends ArrayList<Resolver<F>>
		implements Resolver<F> {

	public CompositeResolver(
			Collection<? extends Resolver<F>> resolvers) {
		super(resolvers);
	}

	public CompositeResolver(Resolver<F>... resolvers) {
		super(Arrays.asList(resolvers));
	}

	@Override
	public boolean appliable(Plan plan) {
		boolean appliable = true;
		int i = 0;
		for (; i < this.size() - 1; i++) {
			Resolver<F> resolver = this.get(i);
			if (!resolver.appliable(plan)) {
				appliable = false;
				break;
			} else {
				resolver.apply(plan);
			}
		}
		appliable &= get(i--).appliable(plan);
		for (; i != 0; i--) {
			get(i).revert();
		}
		return appliable;
	}

	@Override
	public void apply(Plan plan) {
		for (Resolver<F> resolver : this) {
			resolver.apply(plan);
		}
	}

	@Override
	public void revert() {
		for (int i = size() - 1; i != 0; i--) {
			get(i).revert();
		}
	}

	@Override
	public Collection<Change> changes() {
		Collection<Change> changes = list();
		for (Resolver<F> resolver : this) {
			changes.addAll(resolver.changes());
		}
		return changes;
	}

	@Override
	public State<F> provides() {
		State<F> provides = new State<F>();
		for (Resolver<F> resolver : this) {
			provides.addAll(resolver.provides());
		}
		return provides;
	}

}
