/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.genn.color;

import io.genn.world.CompilationException;
import io.genn.world.Flow;
import io.genn.world.World;
import io.genn.world.data.Entity;
import io.genn.world.data.Store;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Set;
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
			Log.i("Opening the world");
			World world = new World("data/blocks.w");
			Log.i("Compiling...");
			world.compile();
			Log.i("Tests begins");
			test(world.flow);
		} catch (FileNotFoundException | CompilationException ex) {
			Log.f(ex);
		}
	}
	
	private static void test(Flow flow) {
		Store s = flow.store;
		Set<Entity> actions = new HashSet<>();
		for (Entity statement : s.querry(null, flow.interpreter.named("pre"))) {
			actions.add(flow.store.subject(statement));
		}
		for (Entity statement : s.querry(null, flow.interpreter.named("eff"))) {
			actions.add(s.subject(statement));
		}
		Log.i("Fetched actions " + actions);
	}
}
