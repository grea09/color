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
public class Scalability {

    public static void main(String[] args) throws FileNotFoundException {
        Log.level = Log.Level.WARNING;
        Chrono chrono = new Chrono();
        int number = 100;
        File file = new File("data/" + LocalDateTime.now() + "_" + Scalability.class + ".csv");
        System.out.println(file.getAbsolutePath());
        try (PrintStream result = new PrintStream(file)) {
            for (int enthropy = 5; enthropy < 20; enthropy++) {
                for (int hardness = 0; hardness < enthropy / 2; hardness++) {
                    Log.level = Log.Level.FATAL;
                    Set<Problem> problems = ProblemGenerator.generate(number, enthropy, hardness);
                    Log.level = Log.Level.VERBOSE;
                    int popCount = 0;
                    long popQ = 0;
                    long popExp = 0;
                    for (Problem problem : problems) {
                        Log.i("POP : problem " + enthropy + "|" + hardness + "#" + popCount++);
                        problem.clear();
                        chrono.start();
                        if (!PartialOrderPlanning.solve(problem)) {
                            Log.f("FAILLURE !!! Problem :" + problem);
                            popCount--;
                            continue;
                        }
                        long elapsed = chrono.stop();
                        SolutionChecker.display(problem);
                        if (!SolutionChecker.check(problem)) {
                            chrono.total -= elapsed;
                            popCount--;
                            continue;
                        }
                        popQ += problem.plan.vertexSet().size();
                        popExp += problem.expectedLength;
                    }
                    long pop = chrono.total;
                    System.out.println(enthropy + " => POP " + pop);
                    chrono.reset();

                    int lolCount = 0;
                    long lolQ = 0;
                    long lolExp = 0;
                    for (Problem problem : problems) {
                        Log.i("LOL : problem " + enthropy + "|" + hardness + "#" + lolCount++);
                        chrono.start();
                        if (!Lollipop.solve(problem)) {
                            Log.f("FAILLURE !!! Problem :" + problem);
                            lolCount--;
                            continue;
                        }
                        long elapsed = chrono.stop();
                        SolutionChecker.display(problem);
                        if (!SolutionChecker.check(problem)) {
                            chrono.total -= elapsed;
                            popCount--;
                            continue;
                        }
                        lolQ += problem.plan.vertexSet().size();
                        lolExp += problem.expectedLength;
                    }
                    long lol = chrono.total;
                    System.out.println(enthropy + " => LOL " + lol);
                    System.out.println(enthropy + " => diff " + (pop - lol));

                    result.println(enthropy + "," + hardness + "," + pop + "," + lol + "," + popQ + "," + lolQ + "," + popExp + "," + lolExp);
                    result.flush();
                    chrono.reset();
                }
            }
            result.println("Iterations/enthropy+hardness ," + number);
            result.close();
        }
    }
}
