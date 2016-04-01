/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.lollipop.benchmark;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintStream;
import java.io.Reader;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import me.grea.antoine.lollipop.algorithm.PartialOrderPlanning;
import me.grea.antoine.lollipop.persistence.ActionSerializer;
import me.grea.antoine.lollipop.persistence.ProblemSerializer;
import me.grea.antoine.lollipop.type.Action;
import me.grea.antoine.lollipop.type.Problem;
import me.grea.antoine.utils.Chrono;
import me.grea.antoine.utils.Log;
import me.grea.antoine.utils.Strings;

/**
 *
 * @author antoine
 */
public class Benchmark {

    static {
        Log.level = Log.Level.FATAL;
    }

    public static void main(String[] args) throws FileNotFoundException {
        GsonBuilder bob = new GsonBuilder();
        bob.registerTypeAdapter(Action.class, new ActionSerializer());
        bob.registerTypeAdapter(Problem.class, new ProblemSerializer());
        Gson gson = bob.create(); //Damn son

        Chrono chrono = new Chrono();
        File output = new File("results/" + LocalDateTime.now() + "_" + PartialOrderPlanning.class + ".csv");
        System.out.println(output.getAbsolutePath());
        try (PrintStream result = new PrintStream(output)) {
            for (File data : new File("data").listFiles()) {
                Set<Problem> problems = gson.fromJson(new FileReader(data), new TypeToken<Set<Problem>>() {
                }.getType());
                
                String[] extract = Strings.extract(data.getName(), "\\[(\\d+),(\\d+)\\]").get(0);
                int enthropy = Integer.parseInt(extract[0]);
                int hardness = Integer.parseInt(extract[1]);
                
                double quality = 0;

                for (Problem problem : problems) {
                    chrono.start();
                    PartialOrderPlanning.solve(problem);
                    chrono.stop();
                    quality += problem.plan.vertexSet().size() - problem.expectedLength;
                }
                quality /= problems.size();
                System.out.println(enthropy + "," + hardness + "[" + quality + "] => " + chrono);
                result.println(enthropy + "," + hardness + "," + quality + "," + chrono.total / problems.size());
                chrono.reset();
                result.close();

            }
        } catch (FileNotFoundException ex) {
            Log.e(ex);
        }
    }
}
