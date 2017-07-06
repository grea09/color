/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.genn.color.planning.domain;

import io.genn.world.CompilationException;
import io.genn.world.Flow;
import io.genn.world.data.Entity;
import io.genn.world.data.Store;
import static io.genn.world.data.Types.GROUP;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import static me.grea.antoine.utils.collection.Collections.list;
import me.grea.antoine.utils.log.Log;

/**
 *
 * @author antoine
 */
public class WorldFluent implements Fluent<WorldFluent> {

	public final Entity statement;
	public final Entity subject;
	public final Entity property;
	public final Entity object;
	private final Store s;
	private final Entity NOT;//FIXME Entities.NULL once Lang is ready
	private final Flow flow;

	public WorldFluent(Entity statement, Flow flow) {
		this.flow = flow;
		this.s = flow.store;
		this.NOT = s.named("~"); //FIXME UGLY
		this.statement = statement;
		this.subject = s.subject(statement);
		this.property = s.property(statement);
		this.object = s.object(statement);
	}

	@Override
	public boolean unifies(WorldFluent lesser) {
		return s.unify(lesser.statement, statement) != null;
	}

	@Override
	public boolean contradicts(WorldFluent counter) {
		if (equals(counter)) {
			return false;
		}
		boolean negative = negative();
		if (negative == counter.negative()) {
			return false;
		}
		List<Entity> comparison;
		if (negative) {
			comparison = new ArrayList(s.signature(property));
		} else {
			comparison = new ArrayList(s.signature(counter.property));
		}
		if (comparison.size() != 2) {
			return false;
		}
		if (!comparison.get(0).equals(comparison.get(1))) {
			return false;
		}
		if (s.unify(counter.subject, subject) == null) {
			return false;
		}
		if (s.unify(counter.object, object) == null) {
			return false;
		}
		return true;
	}

	@Override
	public WorldFluent negate() {
		try {
			return new WorldFluent(
					s.statement(subject,
								flow.instanciate(NOT, list(property)),
								object),
					flow);
		} catch (CompilationException ex) {
			Log.f(ex);
		}
		return null;
	}

	@Override
	public boolean negative() {
		return NOT.equals(s.general(property));
	}

	@Override
	public String toString() {
		return statement.toString();
	}

}
