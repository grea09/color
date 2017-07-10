/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.genn.color.planning.domain;

import io.genn.color.planning.domain.WorldControl.Quantifier;
import static io.genn.color.planning.domain.WorldControl.Quantifier.*;
import io.genn.world.CompilationException;
import io.genn.world.Flow;
import io.genn.world.data.Entity;
import io.genn.world.data.Store;
import io.genn.world.data.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import static me.grea.antoine.utils.collection.Collections.*;
import me.grea.antoine.utils.log.Log;

/**
 *
 * @author antoine
 */
public class WorldFluent implements Fluent<WorldFluent, Entity> {

	public final Entity image;
	public final Entity property;
	public final List<Entity> parameters;
	private final Flow flow;
	private final Store s;
	private final WorldControl worldControl;
	private final boolean statement;

	private final boolean negative;

	public WorldFluent(Entity image, Flow flow, WorldControl worldControl) {
		this.flow = flow;
		this.s = flow.store;
		this.worldControl = worldControl;

		this.image = image;
		this.statement = Types.STATEMENT.equals(s.type(image));
		Entity property = statement ? s.property(image) : image;
		if (NULL.image().equals(s.general(property))) {
			this.property = first(s.signature(property));
			this.negative = true;
		} else {
			this.property = property;
			this.negative = false;
		}
		if (statement) {
			this.parameters = Collections.unmodifiableList(
					list(s.subject(image), s.object(image)));
		} else {
			this.parameters = Collections.unmodifiableList(new ArrayList<>(s.
					signature(property)));
		}
	}

	@Override
	public boolean unifies(WorldFluent lesser) {
		return unify(lesser) != null;
	}

	@Override
	public boolean contradicts(WorldFluent counter) {
		if (equals(counter) || !property.equals(counter.property)) {
			return false;
		}
		Quantifier filter = Quantifier.and(worldControl.fluents.get(s.general(
				property)));
		Iterator<Entity> sourcesIt = counter.parameters.iterator();
		Iterator<Entity> targetsIt = parameters.iterator();
		boolean matches = true;
		while (sourcesIt.hasNext() && targetsIt.hasNext()) {
			Entity source = sourcesIt.next();
			Entity target = targetsIt.next();
			if (NULL.image().equals(source) || NULL.image().equals(target)) {
				return !(NULL.image().equals(source) &&
						NULL.image().equals(target));
			}

			switch (filter) {
				case NULL:
					return true;
				case UNIQ:
					matches &= s.unify(source, target) != null;
				case SMTH:
					continue;
				case ALL:
					return false;
			}
		}

		return matches & !(negative ^ counter.negative);
	}

	@Override
	public WorldFluent negate() {//FIXME redo
		try {
			Entity property = negative ?
							  this.property :
							  flow.instanciate(NULL.image(),
											   list(this.property));

			if (statement) {
				return new WorldFluent(
						s.statement(parameters.get(0),
									property,
									parameters.get(1)),
						flow, worldControl);
			} else {
				return new WorldFluent(property, flow, worldControl);
			}
		} catch (CompilationException ex) {
			Log.f(ex);
		}
		return null;
	}

	@Override
	public boolean negative() {
		return negative;
	}

	@Override
	public String toString() {
		return image.toString();
	}

	@Override
	public Map<Entity, Entity> unify(WorldFluent lesser) {//FIXME redo
		if (!s.general(property).equals(s.general(lesser.property))) {
			return null;
		}
		Map<Entity, Entity> unify = s.unify(lesser.parameters, parameters);
		if (unify != null &&
				(unify.containsKey(NULL.image()) ||
				unify.containsValue(NULL.image()))) {
			return null;
		}

		return unify;
	}

	@Override
	public FluentControl<WorldFluent, Entity> control() {
		return worldControl == null ? new WorldControl(flow) : worldControl;
	}

}
