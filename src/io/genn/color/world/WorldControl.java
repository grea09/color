/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.genn.color.world;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import io.genn.color.planning.domain.Action;
import io.genn.color.planning.domain.Domain;
import io.genn.color.planning.problem.Problem;
import io.genn.color.planning.domain.fluents.FluentControl;
import io.genn.color.planning.domain.State;
import io.genn.color.planning.problem.CausalLink;
import io.genn.color.planning.problem.Plan;
import io.genn.color.pop.Pop;
import io.genn.color.pop.problem.SimpleSolution;
import io.genn.world.CompilationException;
import io.genn.world.Flow;
import io.genn.world.data.Entity;
import io.genn.world.data.Store;
import static io.genn.world.lang.Types.GROUP;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
			if (conjonction == null || conjonction.isEmpty()) {
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
	private final Entity CONSTR;
	private final Entity COST;
	private final Entity METHOD;
	public final Entity EQUALITY;
	public final Entity SOLVE;

	public static final Multimap<String, Action<WorldFluent, Entity>> actions =
			HashMultimap.create();
	public static final Map<Entity, Plan> methods = new HashMap<>();

	public WorldControl(Flow flow) {
		this.flow = flow; //TODO get rid of flow
		this.s = flow.store;
		this.PRE = flow.interpreter.named("pre");
		this.EFF = flow.interpreter.named("eff");
		this.CONSTR = flow.interpreter.named("constr");
		this.COST = flow.interpreter.named("costs");
		this.METHOD = flow.interpreter.named("method");
		this.EQUALITY = flow.interpreter.named(":");
		this.SOLVE = flow.interpreter.named("?");
		Quantifier.init(s);
		fluents = new HashMap<>();
	}

	public Problem problem() throws CompilationException {
		Domain domain = domain();
		//			Log.i("We have :\n" + domain);
		Action<WorldFluent, Entity> initial = null;
		Action<WorldFluent, Entity> goal = null;
		for (Action<WorldFluent, Entity> action : domain) {
			if (action.goal()) {
				goal = action;
			}
			if (action.initial()) {
				initial = action;
			}
		}
		domain.remove(initial);
		domain.remove(goal);
		if (initial == null) {
			initial = action(flow.create("init"));
		}
		if (goal == null) {
			goal = action(flow.create("goal"));
		}
		Problem problem = new Problem(initial, goal, domain);
		return problem;
	}

	public Domain domain() throws CompilationException {
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

		Set<Entity> actions = new HashSet<>();

		for (Entity statement : s.querry(null, PRE)) {
			if (s.isGlobal(statement)) {
				actions.add(s.subject(statement));
			}
		}
		for (Entity statement : s.querry(null, EFF)) {
			if (s.isGlobal(statement)) {
				actions.add(s.subject(statement));
			}
		}
		for (Entity statement : s.querry(null, CONSTR)) {
			if (s.isGlobal(statement)) {
				actions.add(s.subject(statement));
			}
		}

		Domain domain = new Domain();
		for (Entity action : actions) {
//			Entity parameters = s.parameters(action);
//			if (parameters != null) {
//				boolean proper = true;
//				for (Entity parameter : (List<Entity>) s.value(parameters)) {
//					proper &= s.scopeOf(parameter).contains(action);
//				}
//				if (!proper) {
//					continue;
//				}
//			}
			domain.add(action(action));
		}

		actions.clear();
		for (Entity statement : s.querry(null, METHOD)) {
			if (s.isGlobal(statement)) {
				domain.add(action(s.subject(statement)));
			}
		}

		return domain;
	}

	public State<WorldFluent> state(Entity collection) {
		if (collection != null) {
			Collection<Entity> fluents = s.value(collection);
			if (fluents != null) {
				State<WorldFluent> state = new State<>();
				for (Entity fluent : fluents) {
//					assert (STATEMENT.equals(s.type(fluent)));
					if (s.general(fluent).equals(SOLVE)) {
						continue;
					}
					WorldFluent f = null;
					f = new WorldFluent(fluent, flow, this);
//					if (!state.contradicts(f)) {
					state.add(f);
//					}
				}
				return state;
			}
		}
		return new State<>();
	}

	public Action<WorldFluent, Entity> action(Entity action) throws CompilationException {
		String name = s.name(action);
		List<Entity> parameters = s.signature(action);
		Collection<Action<WorldFluent, Entity>> cached = actions.get(name);
		for (Action existing : cached) {
			if (existing.parameters == parameters ||
					(existing.parameters != null &&
					existing.parameters.equals(parameters))) {
				return existing;
			} else if (!existing.initial() && !existing.goal()) {
				Map unify = s.unify(parameters, existing.parameters);
				if (unify != null && !unify.isEmpty()) {
					Action result = existing.instanciate(unify);
					actions.put(name, result);
					return result;
				}
			}
		}

		State pre = state(s.object(first(s.querry(action, PRE))));
		State eff = state(s.object(first(s.querry(action, EFF))));
		State constr = state(s.object(first(s.querry(action, CONSTR))));
		Entity costEntity = s.object(first(s.querry(action, COST)));
		double cost = costEntity == null ? 1.0 : s.value(costEntity);
		Plan method = method(s.object(first(s.querry(action, METHOD))));//FIXME multiple methods later

		if (method != null) {
			for (Action step : method.vertexSet()) {
				if (step.initial()) {
					pre.addAll(step.eff);
				}
				if (step.goal()) {
					eff.addAll(step.pre);
				}
			}
		}

		Action<WorldFluent, Entity> result =
				new Action(name,
						   parameters,
						   pre,
						   eff,
						   constr, cost,
						   name.equals("init") ? Action.Flag.INIT :
						   (name.equals("goal") ? Action.Flag.GOAL :
							Action.Flag.NORMAL),
						   action, method, null, this);
		actions.put(name, result);
		return result;
	}

	@Override
	public Entity instanciate(
			Action<WorldFluent, Entity> lifted, List<Entity> parameters) {
		Entity action = s.parameterize(lifted.image, parameters);
		return action;
	}

	@Override
	public Entity instanciate(
			Entity entity, Map<Entity, Entity> unify) {
		List<Entity> parameters = (List<Entity>) s.value(s.parameters(entity));
		if (parameters == null || parameters.isEmpty()) {
			return unify.containsKey(entity) ? unify.get(entity) : entity;
		}
		List<Entity> newParameters = new ArrayList<>();
		for (Entity parameter : parameters) {
			newParameters.add(instanciate(parameter, unify));
		}
		if (parameters.equals(newParameters)) {
			return entity;
		}
		Entity general = s.general(entity);
		if (general != null && s.instances(general) != null && s.instances(
				general).containsKey(newParameters)) {
			return s.instances(general).get(newParameters);
		}

		Entity newEntity;
		try {
			newEntity = flow.instanciate(entity, newParameters);
		} catch (CompilationException ex) {
			Log.e(ex);
			return null;
		}
		return newEntity;
	}

	@Override
	public boolean discard(WorldFluent fluent) {
		return (SOLVE.equals(fluent.property));
	}

	private Plan method(Entity composite) throws CompilationException {
		if (composite == null) {
			return null;

		}
		if (methods.containsKey(composite)) {
			return methods.get(composite);
		}

		Plan method = new Plan();
		Action init = null, goal = null;
		for (Entity link : (List<Entity>) s.value(composite)) {
			Action<WorldFluent, Entity> source = action(s.subject(link));
			Action<WorldFluent, Entity> target = action(s.object(link));
			CausalLink causalLink = method.addEdge(source, target);
			Entity parameters = s.parameters(s.property(link));
			if (parameters != null) {
				causalLink.causes.addAll(state(parameters));
			} else {
				for (WorldFluent eff : source.eff) {
					for (WorldFluent pre : target.pre) {
						if (eff.unifies(pre)) {
							causalLink.causes.add(pre);
						}
					}
				}

				if (source.initial()) {
					init = source;
					source.eff.addAll(target.pre); //FIXME Check for inconsistencies
					causalLink.causes.addAll(target.pre);
//					source.pre.addAll(source.eff); //FIXME is that good ?
				}
				if (target.goal()) {
					goal = target;
					target.pre.addAll(source.eff); //FIXME Check for inconsistencies
					causalLink.causes.addAll(target.pre);
//					target.eff.addAll(target.pre); //FIXME is that good ?
				}
			}
		}
		if (init != null && goal != null) {
			method.addEdge(init, goal);
		} else {
			throw new IllegalStateException(
					"Methods must have an initial and goal action !");
		}
		Problem problem = new Problem(init, goal, new Domain());
		problem.solution = new SimpleSolution(method);
		boolean ENABLED = Log.ENABLED;
		Log.ENABLED = false;
		boolean success = false;
		try {
			success = new Pop(problem).solve();
		} catch (InterruptedException ex) {
			Log.w(ex);
		}
		Log.ENABLED = ENABLED;
		if (!success) {
			Log.w("Can't solve method of " + composite +
					" this might explain longer execution times.");
		}
		init.pre.addAll(init.eff);
		goal.eff.addAll(goal.pre);
		methods.put(composite, method);
		return method;
	}

}
