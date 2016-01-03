/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.soda.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.Set;
import me.grea.antoine.soda.algorithm.PartialOrderOptimizedPlanning;
import me.grea.antoine.soda.type.Problem;
import me.grea.antoine.utils.Chrono;
import me.grea.antoine.utils.Log;

/**
 *
 * @author antoine
 */
public class RandomTest {
    
    public static void main(String... args)
    {
        Log.level = Log.Level.FATAL;
        Chrono chrono = new Chrono();
        int number = 10000;
//        File file = new File(LocalDateTime.now() + "_" + RandomTest.class + ".csv");
//        System.out.println(file.getAbsolutePath());
//        try (PrintStream result = new PrintStream(file)) {
            //echaufement
            Set<Problem> problems = CrazyProblemGenerator.generate(40, 10000);
            for (Problem problem : problems) {
                PartialOrderOptimizedPlanning.solve(problem);
            }
//            for (int i = 1; i < 100; i++) {
//                problems = CrazyProblemGenerator.generate(i, number);
//                for (Problem problem : problems) {
//                    chrono.start();
//                    PartialOrderOptimizedPlanning.solve(problem);
//                    chrono.stop();
//                }
//                System.out.println(i + " => " + chrono);
////                result.println(i + "," + chrono.total / number);
//                chrono.reset();
//            }
//            result.println("Iterations/enthropy : " + number);
//            result.close();
//        } catch (FileNotFoundException ex) {
//            Log.e(ex);
//        }
    }
    
}
