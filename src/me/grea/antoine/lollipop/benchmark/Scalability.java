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

    public static void main(String[] args) {
//        Log.level = Log.Level.WARNING;
        Chrono chrono = new Chrono();
        int number = 1000;
//        File file = new File(LocalDateTime.now() + "_" + Scalability.class + ".csv");
//        System.out.println(file.getAbsolutePath());
//        try (PrintStream result = new PrintStream(file)) {
        for (int i = 5; i < 20; i++) {
            Log.level = Log.Level.FATAL;
            Set<Problem> problems = ProblemGenerator.generate(number, i, (int) (i * 0.3));
            Log.level = Log.Level.VERBOSE;
            int count = 0;
            for (Problem problem : problems) {
                Log.i("LOL : problem #" + count++);
                chrono.start();
                if (!Lollipop.solve(problem)) {
                    Log.f("FAILLURE !!! Problem :" + problem);
                }
                chrono.stop();
                SolutionChecker.display(problem);
            }
            long pop = chrono.total / number;
//                System.out.println(i + " => POP " + chrono.total / number);
            chrono.reset();

            count = 0;
            for (Problem problem : problems) {
                Log.i("POP : problem #" + count++);
                problem.clear();
                chrono.start();
                if (!PartialOrderPlanning.solve(problem)){
                    Log.f("FAILLURE !!! Problem :" + problem);
                }
                chrono.stop();
                SolutionChecker.display(problem);
            }
//                System.out.println(i + " => LOL " + chrono.total / number);
            long lol = chrono.total / number;
//                result.println(i + "," + chrono.total / number);
            chrono.reset();

            System.out.println(i + " => diff " + (pop - lol));
        }
//            result.println("Iterations/enthropy : " + number);
//            result.close();
//        } catch (FileNotFoundException ex) {
//            Log.e(ex);
//        }
    }
}
