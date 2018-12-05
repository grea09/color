/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.genn.color.planning.domain;

import io.genn.color.heart.Heart;
import io.genn.color.planning.algorithm.Planner;
import io.genn.color.planning.problem.CausalLink;
import io.genn.color.planning.problem.Plan;
import io.genn.color.planning.problem.Problem;
import io.genn.color.world.WorldControl;
import io.genn.color.world.WorldFluent;
import io.genn.world.CompilationException;
import io.genn.world.World;
import io.genn.world.data.Entity;
import io.genn.world.lang.Types;
import java.io.FileNotFoundException;
import java.util.List;
import static me.grea.antoine.utils.collection.Collections.list;
import static me.grea.antoine.utils.collection.Collections.first;
import static me.grea.antoine.utils.collection.Collections.last;
import me.grea.antoine.utils.log.Log;

/**
 *
 * @author antoine
 */
public class Generator {

	private static final char FIRST = 'a';
	private static final char FLUENT = 'f';

	public static Problem problem(int lv, int spread) {
		try {
			World world = new World("data/lite.w");
			world.compile(false);

			WorldControl control = new WorldControl(world.flow);
			Domain domain = new Domain();
			List<Action<WorldFluent, Entity>> actions = generate("", lv, spread,
																 domain, world,
																 control);
			Action init = new Action("init", null, new State<>(), new State<>(),
									 new State<>(), 1,
									 Action.Flag.INIT, world.flow.create(
											 "init"), null,
									 control);
			Action goal = new Action("goal", null, first(actions).eff,
									 new State<>(),
									 new State<>(), 1,
									 Action.Flag.GOAL, world.flow.create(
											 "goal"), null,
									 control);
			world.flow.store.global();
			return new Problem(init, goal, domain);
		} catch (FileNotFoundException | CompilationException ex) {
			Log.f(ex);
			return null;
		}
	}

	private static List<Action<WorldFluent, Entity>> generate(String prefix,
			int level, int spread, Domain domain, World world,
			WorldControl control) throws CompilationException {
		List<Action<WorldFluent, Entity>> actions = list();
		if (level == 0) {
			for (int i = 0; i < spread; i++) {
				String name = "" + prefix + (char) (FIRST + i);
				actions.add(new Action<>(name, null, new State<>(),
										 new State<>(new WorldFluent(world.flow.
												 create("" + FLUENT + name),
																	 world.flow,
																	 control)),
										 new State<>(), 1,
										 Action.Flag.NORMAL, world.flow.create(
												 name), null, control));
			}

		} else {
			for (int i = 0; i < spread && (!prefix.isEmpty()||i==0); i++) {
				String name = "" + prefix + (char) (FIRST + i);
				Entity action = world.flow.create(name);
				world.flow.store.global();
				Plan method = new Plan();
				List<Action<WorldFluent, Entity>> steps =
						generate(name, level - 1, spread, domain, world, control);
				State<WorldFluent> pre = first(steps).pre;
				State<WorldFluent> eff = last(steps).eff;
				Action init = new Action("init", list(action), pre, pre,
										 new State<>(), 1,
										 Action.Flag.INIT, world.flow.create(
												 "init", world.flow.create(
														 Types.GROUP, list(
																 action))), null,
										 control);
				Action goal = new Action("goal", list(action), eff, eff,
										 new State<>(), 1,
										 Action.Flag.GOAL, world.flow.create(
												 "goal", world.flow.create(
														 Types.GROUP, list(
																 action))), null,
										 control);
				method.addEdge(init, goal);
				steps.add(0, init);
				steps.add(spread + 1, goal);
				Action<WorldFluent, Entity> source = init;

				for (Action<WorldFluent, Entity> target : steps) {
					if (source.equals(target)) {
						continue;
					}
					CausalLink causalLink = method.addEdge(source, target);
					for (WorldFluent eff_ : source.eff) {
						for (WorldFluent pre_ : target.pre) {
							if (eff_.unifies(pre_)) {
								causalLink.causes.add(pre_);
							}
						}
					}

					if (source.initial()) {
						source.eff.addAll(target.pre); //FIXME Check for inconsistencies
						causalLink.causes.addAll(target.pre);
//					source.pre.addAll(source.eff); //FIXME is that good ?
					}
					if (target.goal()) {
						target.pre.addAll(source.eff); //FIXME Check for inconsistencies
						causalLink.causes.addAll(target.pre);
//					target.eff.addAll(target.pre); //FIXME is that good ?
					}
					source = target;
				}

				actions.add(new Action<>(name, null, pre, eff, new State<>(), 1,
										 Action.Flag.NORMAL, action, method,
										 null,
										 control));

			}
		}
		domain.addAll(actions);
		return actions;
	}
}
