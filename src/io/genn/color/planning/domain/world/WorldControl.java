/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.genn.color.planning.domain.world;

import io.genn.color.planning.domain.Action;
import io.genn.color.planning.domain.Domain;
import io.genn.color.planning.domain.fluents.FluentControl;
import io.genn.color.planning.domain.State;
import io.genn.color.planning.domain.world.WorldFluent;
import io.genn.color.planning.plan.CausalLink;
import io.genn.color.planning.plan.Plan;
import io.genn.world.CompilationException;
import io.genn.world.Flow;
import io.genn.world.data.Entity;
import io.genn.world.data.Store;
import static io.genn.world.lang.Types.GROUP;
import static io.genn.world.lang.Types.STATEMENT;
import io.genn.world.inferer.InferenceException;
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
	private final Entity METHOD;
	public final Entity EQUALITY;
	public final Entity SOLVE;

	public WorldControl(Flow flow) {
		this.flow = flow; //TODO get rid of flow
		this.s = flow.store;
		this.PRE = flow.interpreter.named("pre");
		this.EFF = flow.interpreter.named("eff");
		this.CONSTR = flow.interpreter.named("constr");
		this.METHOD = flow.interpreter.named("method");
		this.EQUALITY = flow.interpreter.named(":");
		this.SOLVE = flow.interpreter.named("?");
		Quantifier.init(s);
		fluents = new HashMap<>();
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

		Map<Entity, Entity> pres = new HashMap<>();
		Map<Entity, Entity> effs = new HashMap<>();
		Map<Entity, Entity> constrs = new HashMap<>();
		for (Entity statement : s.querry(null, PRE)) {
			pres.put(s.subject(statement), s.object(statement));
		}
		for (Entity statement : s.querry(null, EFF)) {
			effs.put(s.subject(statement), s.object(statement));
		}
		for (Entity statement : s.querry(null, CONSTR)) {
			constrs.put(s.subject(statement), s.object(statement));
		}
		Set<Entity> actions = new HashSet<>();
		for (Entity action : union(pres.keySet(), effs.keySet())) {
			if (s.parameters(action) == null ||
					!s.scope(action).isEmpty()) {
				actions.add(action);
			}
		}
		Domain domain = new Domain();
		for (Entity action : actions) {
			domain.add(action(action, pres.get(action), effs.get(action), 
														constrs.get(action)));
		}

		pres.clear();
		effs.clear();
		constrs.clear();
		Map<Entity, Entity> methods = new HashMap<>();
		for (Entity statement : s.querry(null, METHOD)) {
			methods.put(s.subject(statement), s.object(statement));
		}
		for (Map.Entry<Entity, Entity> entry : methods.entrySet()) {
			Entity action = entry.getKey();
			Entity method = entry.getValue();
			//Parse method
			Plan plan = plan(method);

			domain.add(action(action, plan));
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

	public Action<WorldFluent, Entity> action(Entity action, Entity pre,
			Entity eff, Entity constr) {
		return action(action, state(pre), state(eff), state(constr));
	}

	public Action<WorldFluent, Entity> action(Entity action, State pre,
			State eff, State constr) {
		return action(action, pre, eff, constr, null);
	}

	public Action<WorldFluent, Entity> action(Entity action, State pre,
			State eff, State constr, Plan method) {
		String name = s.name(action);
		return new Action(s.name(action),
						  s.signature(action),
						  pre,
						  eff,
						  constr,
						  name.equals("init") ? Action.Flag.INIT :
						  (name.equals("goal") ? Action.Flag.GOAL :
						   Action.Flag.NORMAL),
						  action, method, null, this);
	}

	public Action<WorldFluent, Entity> action(Entity action) throws CompilationException {
		return action(action, null);
	}

	public Action<WorldFluent, Entity> action(Entity action, Plan method) throws CompilationException {
		Set<Entity> pres = new HashSet<>(), effs = new HashSet<>(), constrs = new HashSet<>();
		for (Entity statement : s.querry(action, PRE)) {
			pres.add(s.object(statement));
		}
		for (Entity statement : s.querry(action, EFF)) {
			effs.add(s.object(statement));
		}
		for (Entity statement : s.querry(action, CONSTR)) {
			constrs.add(s.object(statement));
		}

		State pre = new State(), eff = new State(), constr = new State();
		if (!pres.isEmpty()) {
			for (Entity precondition : pres) {
				pre.addAll(state(precondition));
			}
		}
		if (!effs.isEmpty()) {
			for (Entity effect : effs) {
				eff.addAll(state(effect));
			}
		}
		if (!constrs.isEmpty()) {
			for (Entity constraint : constrs) {
				constr.addAll(state(constraint));
			}
		}

		return action(action, pre, eff, constr, method);
	}

	@Override
	public Entity instanciate(
			Action<WorldFluent, Entity> lifted, List<Entity> parameters) {
		Entity action = s.parameterize(lifted.image, parameters);
		return action;
	}

	@Override
	public boolean discard(WorldFluent fluent) {
		return (SOLVE.equals(fluent.property));
	}

	private Plan plan(Entity method) throws CompilationException {
		Plan plan = new Plan();
		for (Entity link : (List<Entity>) s.value(method)) {
			Action<WorldFluent, Entity> source = action(s.subject(link));
			Action<WorldFluent, Entity> target = action(s.object(link));
			CausalLink causalLink = plan.addEdge(source, target);
			Entity parameters = s.parameters(s.property(link));
			if (parameters != null) {
				causalLink.causes.addAll(state(parameters));
			}
			for (WorldFluent eff : source.eff) {
				for (WorldFluent pre : target.pre) {
					if (pre.unifies(eff)) {
						causalLink.add(eff);
					}
				}
			}
			Action<WorldFluent, Entity> init =
					source.initial() ? source : target.initial() ? target : null;
			Action<WorldFluent, Entity> goal =
					source.goal() ? source : target.goal() ? target : null;
			if (init != null) {
				Set<CausalLink> outs = plan.outgoingEdgesOf(init);
				init.eff.clear();
				for (CausalLink out : outs) {
					init.eff.addAll(out.target().pre);
				}
			}
			if (goal != null) {
				Set<CausalLink> ins = plan.incomingEdgesOf(goal);
				goal.pre.clear();
				for (CausalLink in : ins) {
					goal.pre.addAll(in.source().eff);
				}
			}
		}
		return plan;
	}

}
