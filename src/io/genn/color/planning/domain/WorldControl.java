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
import static io.genn.world.data.Types.STATEMENT;
import io.genn.world.inferer.InferenceException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static me.grea.antoine.utils.collection.Collections.*;
import me.grea.antoine.utils.log.Log;

/**
 *
 * @author antoine
 */
public class WorldControl implements FluentControl<WorldFluent, Entity> {

	/**
	 *
	 * @author antoine
	 */
	public static enum Quantifier {
		//FIXME whith Lang inference
		NULL("~"), UNIQ("!"), SMTH("_"), ALL("*");
		//,SOLVE("?");
		private final String name;
		private Entity image;

		private Quantifier(String name) {
			this.name = name;
		}

		public static void init(Store s) {
			for (Quantifier value : values()) {
				value.image = s.named(value.name);
			}
		}

		public static Quantifier valueOf(Entity quantifier) {
			for (Quantifier value : values()) {
				if (value.image.equals(quantifier)) {
					return value;
				}
			}
			return null;
		}

		public static Quantifier and(Quantifier... conjonction) {
			return and(list(conjonction));
		}

		public static Quantifier and(List<Quantifier> conjonction) {
			if (conjonction.isEmpty()) {
				return null;
			}
			Quantifier result = conjonction.get(0);
			for (int i = 1; i < conjonction.size(); i++) {
				result = result.and(conjonction.get(i));
			}
			return result;
		}

		public Quantifier and(Quantifier other) {
			return values()[ordinal() & other.ordinal()];
		}

		public Entity image() {
			return image;
		}
	}

	public final Flow flow;
	public final Store s;
	public final Map<Entity, List<Quantifier>> fluents;
	private final Entity PRE;
	private final Entity EFF;

	public WorldControl(Flow flow) {
		this.flow = flow;
		this.s = flow.store;
		this.PRE = flow.interpreter.named("pre");
		this.EFF = flow.interpreter.named("eff");
		Quantifier.init(s);
		fluents = new HashMap<>();
	}

	public Domain domain() {
		for (Entity statement : s.querry(null, s.named("::"), s.named("Fluent"))) {
			if (GROUP.equals(s.type(s.subject(statement)))) {
				List<Entity> entities = s.value(s.subject(statement));
				for (Entity fluent : entities) {
					WorldFluent worldFluent =
							new WorldFluent(fluent, flow, this);
					fluents.put(s.general(worldFluent.property),
								new ArrayList<Quantifier>() {
							{
								for (Entity parameter : worldFluent.parameters) {
									Quantifier quantifier =
											Quantifier.valueOf(parameter);
									if (quantifier != null) {
										add(quantifier);
									}
								}
							}
						});
				}
			}
		}

		Map<Entity, Entity> pres = new HashMap<>();
		Map<Entity, Entity> effs = new HashMap<>();
		for (Entity statement : s.querry(null, PRE)) {
			pres.put(s.subject(statement), s.object(statement));
		}
		for (Entity statement : s.querry(null, EFF)) {
			effs.put(s.subject(statement), s.object(statement));
		}
		Domain domain = new Domain();
		for (Entity action : union(pres.keySet(), effs.keySet())) {
			domain.add(action(action, pres.get(action), effs.get(action)));
		}
		return domain;
	}

	public State<WorldFluent> state(Entity collection) {
		if (collection != null) {
			Collection<Entity> fluents = s.value(collection);
			if (fluents != null) {
				State<WorldFluent> state = new State<>();
				for (Entity fluent : fluents) {
					assert (STATEMENT.equals(s.type(fluent)));
					WorldFluent f = null;
					f = new WorldFluent(fluent, flow, this);
					if (!state.contradicts(f)) {
						state.add(f);
					}
				}
				return state;
			}
		}
		return new State<>();
	}

	public Action<WorldFluent, Entity> action(Entity action, Entity pre,
			Entity eff) {
		String name = s.name(action);
		return new Action(s.name(action),
						  s.signature(action),
						  state(pre),
						  state(eff),
						  name.equals("init") ? Action.Flag.INIT :
						  (name.equals("goal") ? Action.Flag.GOAL :
						   Action.Flag.NORMAL),
						  action);
	}

	@Override
	public Action<WorldFluent, Entity> instanciate(
			Action<WorldFluent, Entity> lifted, List<Entity> parameters) {
		Entity action;
		try {
			action = flow.instanciate(lifted.image, parameters);
		} catch (CompilationException ex) {
			Log.e(ex);
			return null;
		}
		Entity pre = null;
		Entity eff = null;
		Collection<Entity> querry = s.querry(action, PRE);
		if (!querry.isEmpty()) {
			pre = s.object(first(querry));
		}
		querry = s.querry(action, EFF);
		if (!querry.isEmpty()) {
			eff = s.object(first(querry));
		}
		Log.v("Instanciated " + lifted + " => " +
				action + " pre " + pre + " eff " + eff);
		return action(action, pre, eff);
	}

}
