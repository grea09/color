/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.lollipop.benchmark;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.Set;
import me.grea.antoine.lollipop.algorithm.Lollipop;
import me.grea.antoine.lollipop.algorithm.PartialOrderPlanning;
import me.grea.antoine.lollipop.type.Problem;
import me.grea.antoine.utils.Chrono;
import me.grea.antoine.utils.Log;

/**
 *
 * @author antoine
 */
public class MiniScalability {

    public static void main(String[] args) throws FileNotFoundException {
        Log.level = Log.Level.WARNING;
        Chrono chrono = new Chrono();
        int number = 100;
        File file = new File("data/" + LocalDateTime.now() + "_" + MiniScalability.class + ".csv");
        System.out.println(file.getAbsolutePath());
        try (PrintStream result = new PrintStream(file)) {
            for (int enthropy = 5; enthropy < 20; enthropy++) {
                for (int hardness = 0; hardness < enthropy / 2; hardness++) {
//                    Log.level = Log.Level.FATAL;
                    Set<Problem> problems = ProblemGenerator.generate(number, enthropy, hardness);
//                    Log.level = Log.Level.VERBOSE;
                    for (Problem problem : problems) {
                        problem.clear();
                        chrono.start();
                        if (!PartialOrderPlanning.solve(problem)) {
                            Log.e("FAILLURE !!! Problem :" + problem);
                            continue;
                        }
                        long elapsed = chrono.stop();
                        if (!SolutionChecker.check(problem)) {
                            chrono.total -= elapsed;
                            continue;
                        }
                        long pop = elapsed;
                        long popQ = problem.plan.vertexSet().size();
                        
                        
                        if (!Lollipop.solve(problem)) {
                            Log.e("FAILLURE !!! Problem :" + problem);
                            continue;
                        }
                        elapsed = chrono.stop();
                        if (!SolutionChecker.check(problem)) {
                            chrono.total -= elapsed;
                            continue;
                        }
                        result.println(enthropy + "," + hardness + "," + pop + "," + popQ + "," + elapsed + "," + problem.plan.vertexSet().size() + "," + problem.expectedLength);
                        result.flush();
                    }
                }
                result.close();
            }
        }
    }
}
