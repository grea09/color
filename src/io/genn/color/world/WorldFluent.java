/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.genn.color.world;

import io.genn.color.planning.domain.Action;
import io.genn.color.planning.domain.fluents.Fluent;
import io.genn.color.planning.domain.fluents.FluentControl;
import io.genn.color.world.WorldControl.Quantifier;
import static io.genn.color.world.WorldControl.Quantifier.*;
import io.genn.world.CompilationException;
import io.genn.world.Flow;
import io.genn.world.data.Entity;
import io.genn.world.data.Store;
import io.genn.world.lang.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
			property = first(s.signature(property));
			this.property = s.general(property);
			this.negative = true;
		} else {
			this.property = s.general(property);
			this.negative = false;
		}
		if (statement) {
			this.parameters = Collections.unmodifiableList(
					list(s.subject(image), s.object(image)));
		} else {
			if (s.signature(property) != null) {
				this.parameters = Collections.unmodifiableList(new ArrayList<>(
						s.signature(property)));
			} else {
				this.parameters = Collections.unmodifiableList(list());
			}
		}
	}

	public WorldFluent(Entity image, Entity property, List<Entity> parameters,
			Flow flow, WorldControl worldControl, boolean statement,
			boolean negative) {
		this.image = image;
		this.property = property;
		this.parameters = parameters;
		this.flow = flow;
		this.s = flow.store;
		this.worldControl = worldControl;
		this.statement = statement;
		this.negative = negative;
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
		if (filter == null) {
			filter = SMTH;
		}
//		Log.v("Contradicting " + this + " -> " + counter);
		Iterator<Entity> sourcesIt = counter.parameters.iterator();
		Iterator<Entity> targetsIt = parameters.iterator();
		boolean negates = (negative ^ counter.negative);
//		Log.v("Negates " + negates);
		boolean matches = true;
		while (sourcesIt.hasNext() && targetsIt.hasNext()) {
			Entity source = sourcesIt.next();
			Entity target = targetsIt.next();

			boolean nul = (NULL.image().equals(source) ||
					NULL.image().equals(target));
			boolean all = (ALL.image().equals(source) ||
					ALL.image().equals(target));

//			Log.v("NULL " + nul + " ALL " + all);
			if (nul && !negates || !nul && all && negates) {
				return true;
			}

			if (nul && negates) {
				return false;
			}

			switch (filter) {
				case NULL:
					return true;
				case UNIQ:
					if (!nul || !all) {
						matches &= s.unify(source, target) != null;
					}
				case SMTH:
					continue;
				case ALL:
					return false;
			}
		}
//		Log.v("Matches " + matches);

		return matches && negates;
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
//		Log.v("Unifying " + this + " -> " + lesser);

		Map<Entity, Entity> unify = s.unify(lesser.parameters, parameters);
		Iterator<Entity> sourcesIt = lesser.parameters.iterator();
		Iterator<Entity> targetsIt = parameters.iterator();
		while (sourcesIt.hasNext() && targetsIt.hasNext()) {
			Entity source = sourcesIt.next();
			Entity target = targetsIt.next();
			boolean nul = (NULL.image().equals(source) ||
					NULL.image().equals(target));
			boolean all = (ALL.image().equals(source) ||
					ALL.image().equals(target));

			if (nul || all) { // FIXME dirrection is important !
				if (unify == null) {
//					Log.v("Forcing " + this + " -> " + lesser);
					unify = new HashMap<>();
				}
//				unify.put(source, target);
			}
		}

		if (unify != null) {
			// Quantifiers handling

			if (((unify.containsKey(NULL.image()) && !negative) ||
					(unify.containsValue(NULL.image()) && !lesser.negative))) {
				Log.v("Something is null in " + this + " -> " + lesser);
				return null;
			}
		}

		return unify;
	}

	@Override
	public FluentControl<WorldFluent, Entity> control() {
		return worldControl == null ? new WorldControl(flow) : worldControl;
	}

	@Override
	public WorldFluent instanciate(Map<Entity, Entity> unify) {
		if (unify == null) {
			return null;
		}
		boolean change = false;
		List<Entity> newParameters = list();
		for (Entity parameter : parameters) {
			if (unify.containsKey(parameter)) {
				change = true;
				newParameters.add(unify.get(parameter));
				continue;
			}
			newParameters.add(parameter);
		}
		if (!change) {
			return this;
		}
		Entity image;
		if (statement) {
			image = s.statement(newParameters.get(0),
								negative ?
								s.parameterize(NULL.image(), list(property)) :
								property,
								newParameters.get(1));
		} else {
			image = s.parameterize(property, newParameters);
			if (negative) {
				image = s.parameterize(NULL.image(), list(image));
			}
		}
		return new WorldFluent(image, property, newParameters,
							   flow, worldControl,
							   statement, negative);
	}

	@Override
	public boolean coherent() {
		if (worldControl.EQUALITY.equals(property)) {
			return negative ^ parameters.get(0).equals(parameters.get(1));
		}
		return true; //FIXME other things ?
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 41 * hash + Objects.hashCode(this.property);
		hash = 41 * hash + Objects.hashCode(this.parameters);
		hash = 41 * hash + Objects.hashCode(this.negative);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final WorldFluent other = (WorldFluent) obj;
//		return image.equals(other.image);
		if (!Objects.equals(this.property, other.property)) {
			return false;
		}
		if (!Objects.equals(this.parameters, other.parameters)) {
			return false;
		}
		if (this.negative != other.negative) {
			return false;
		}
		return true;
	}

}
