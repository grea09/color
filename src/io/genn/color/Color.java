/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.genn.color;

import io.genn.color.pop.Pop;
import io.genn.color.planning.domain.Action;
import io.genn.color.planning.domain.Domain;
import io.genn.color.world.WorldFluent;
import io.genn.color.world.WorldControl;
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
			World world = new World("data/kitchen_multi.w");
			Log.i("Compiling...");
			Log.LEVEL = Log.Level.INFO;
			world.compile(false);
			Log.LEVEL = Log.Level.VERB;
//			Log.CONTEXTUALIZED = true;
			Log.i("It begins !");
			WorldControl control = new WorldControl(world.flow);
			Problem problem = control.problem();
			Pop pop = new Pop(problem);
			pop.solve();
			Log.i(problem.solution);
		} catch (FileNotFoundException | CompilationException ex) {
			Log.f(ex);
		}
	}
}
