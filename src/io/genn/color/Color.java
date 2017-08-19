/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.genn.color;

import io.genn.color.planning.algorithm.pop.Pop;
import io.genn.color.planning.domain.Action;
import io.genn.color.planning.domain.Domain;
import io.genn.color.planning.domain.WorldFluent;
import io.genn.color.planning.domain.WorldControl;
import io.genn.color.planning.problem.Problem;
import io.genn.world.CompilationException;
import io.genn.world.World;
import io.genn.world.data.Entity;
import java.io.FileNotFoundException;
import me.grea.antoine.utils.log.Log;

/**
 *
 * @author antoine
 */
public class Color {

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		try {
			Log.i("Opening the world...");
			World world = new World("data/simple.w");
			Log.i("Compiling...");
			Log.LEVEL = Log.Level.INFO;
			world.compile(false);
			Log.LEVEL = Log.Level.DEBUG;
			Log.i("It begins !");
			WorldControl control = new WorldControl(world.flow);
			Domain domain = control.domain();
			Log.i("We have :\n" + domain);
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
				initial = control.action(world.flow.create("init"));
			}
			if (goal == null) {
				goal = control.action(world.flow.create("goal"));
			}

			Problem problem = new Problem(initial, goal, domain);
			Pop pop = new Pop(problem);
			pop.solve();
			Log.i(problem.plan);
		} catch (FileNotFoundException | CompilationException ex) {
			Log.f(ex);
		}
	}
}
