/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.genn.color;

import io.genn.color.heart.Heart;
import io.genn.color.planning.algorithm.Planner;
import io.genn.color.planning.domain.Action;
import io.genn.color.world.WorldControl;
import io.genn.color.planning.problem.Problem;
import io.genn.color.pop.Pop;
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
	public static void main(String[] args) throws InterruptedException {
		try {
			Log.i("Opening the world...");
			World world = new World("data/blocks.w");
			Log.i("Compiling...");
			Log.LEVEL = Log.Level.INFO;
			world.compile(false);
			Log.LEVEL = Log.Level.DEBUG;
//			Log.CONTEXTUALIZED = true;
			Log.i("Parsing...");
			WorldControl control = new WorldControl(world.flow);
			Problem problem = control.problem();
			for (Action action : problem.domain) {
				Log.d(action.name + action.parameters + "@" + action.level +
						" :\n" +
						"\t pre: " + action.pre + "\n" +
						"\t eff :" + action.eff + "\n" +
						"\t constr: " + action.constr + "\n" +
						"\t method: " + action.method
				);
			}
			Log.i("Running...");
//			Log.ENABLED = false;
//			
			Planner planner;
//			for (int i = 0; i < 100; i++) {
//				planner = new Pop(problem);
//				planner.solve();
//			}

//			problem = control.problem();
			planner = new Pop(problem);
			planner.solve();
			Log.i(problem.solution);
			

//			problem = control.problem();
//			planner = new HiPop(problem);
//			planner.solve();
		} catch (FileNotFoundException | CompilationException ex) {
			Log.f(ex);
		}
	}
}
