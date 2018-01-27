package io.genn.color;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;
import io.genn.color.abort.Abort;
import io.genn.color.planning.algorithm.Planner;
import static io.genn.color.planning.domain.Generator.problem;
import io.genn.color.planning.problem.Problem;
import io.genn.color.world.WorldControl;
import io.genn.world.CompilationException;
import io.genn.world.World;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import static me.grea.antoine.utils.collection.Collections.first;
import me.grea.antoine.utils.log.Log;
import me.grea.antoine.utils.text.Formater;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author antoine
 */
public class Benchmark {

	public static ListMultimap<Problem, Long> stops =
			MultimapBuilder.treeKeys().arrayListValues().build();

	public static void main(String[] args) throws CompilationException, IOException {
		String path =
				"data/benchmarks/" + DateTimeFormatter.ISO_DATE_TIME.format(
						LocalDateTime.now()) + ".csv";
		new File("data/benchmarks/").mkdirs();
		new File(path).createNewFile();
		PrintWriter writer = new PrintWriter(path, "UTF-8");
		writer.println("Max_Level;Spread;Level;Time");

		Log.ENABLED = false;

		int spread = 2;
		int level = 6;
		
		Problem problem = problem(level, spread);
//		Log.i("Opening the world...");
//		World world = new World("data/kitchen_tea.w");
//		Log.i("Compiling...");
//		Log.LEVEL = Log.Level.INFO;
//		world.compile(false);
//		Log.LEVEL = Log.Level.DEBUG;
////			Log.CONTEXTUALIZED = true;
//		Log.i("Parsing...");
//		WorldControl control = new WorldControl(world.flow);
//		Problem problem = control.problem();
		
		for (int a = 0; a < 1; a++) {
			Planner planner = new Abort(problem);
			stops.put(problem, System.nanoTime());
			planner.solve();
//			stops.put(problem, System.nanoTime());
			Log.ENABLED = true;
			Log.i("#Interation : " + a);
			List<Long> chrono = stops.get(problem);
			Long start = first(chrono);

			for (int i = 1; i < chrono.size(); i++) {
				String line = "" + ";" + (level - i) +
						";" +
						(chrono.get(i) - start);
//				String line = "" + level + ";" + spread + ";" + (level - i) +
//						";" +
//						(chrono.get(i) - start);
				writer.println(line);
				Log.i(line);
			}
			stops.clear();

			Log.ENABLED = false;
		}

		writer.flush();
		writer.close();
	}

}
