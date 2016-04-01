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
        Log.level = Log.Level.FATAL;
        Chrono chrono = new Chrono();
        int number = 10000;
        File file = new File(LocalDateTime.now() + "_" + Scalability.class + ".csv");
        System.out.println(file.getAbsolutePath());
        try (PrintStream result = new PrintStream(file)) {
            for (int i = 3; i < 100; i++) {
                Set<Problem> problems = ProblemGenerator.generate(number, i, i/2);
                for (Problem problem : problems) {
                    chrono.start();
                    PartialOrderPlanning.solve(problem);
                    chrono.stop();
                }
                System.out.println(i + " => " + chrono);
                result.println(i + "," + chrono.total / number);
                chrono.reset();
            }
            result.println("Iterations/enthropy : " + number);
            result.close();
        } catch (FileNotFoundException ex) {
            Log.e(ex);
        }
    }
}
