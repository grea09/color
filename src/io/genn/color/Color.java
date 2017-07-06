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
import io.genn.color.planning.problem.Problem;
import io.genn.world.CompilationException;
import io.genn.world.World;
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
			World world = new World("data/blocks.w");
			Log.i("Compiling...");
			world.compile();
			Log.i("Parsing begins !");
			Domain<WorldFluent> domain = new Domain<>(world.flow,
													  WorldFluent.class);
			Log.i("Domain :\n" + domain);
			Action<WorldFluent> initial = null;
			Action<WorldFluent> goal = null;
			for (Action<WorldFluent> action : domain) {
				if (action.goal()) {
					goal = action;
				}
				if (action.initial()) {
					initial = action;
				}
			}
			domain.remove(initial);
			domain.remove(goal);

			Problem<WorldFluent> problem = new Problem(initial, goal, domain);
			Pop<WorldFluent> pop = new Pop<>(problem);
			pop.solve();
		} catch (FileNotFoundException | CompilationException ex) {
			Log.f(ex);
		}
	}
}
