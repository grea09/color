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
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.grea.antoine.log.Log;
import me.grea.antoine.soda.algorithm.Soda;
import me.grea.antoine.soda.exception.Failure;
import me.grea.antoine.soda.type.Problem;

/**
 *
 * @author antoine
 */
public class ScalabilityOfSoda {

    public static void main(String[] args) throws Failure {
        Log.level = Log.Level.FATAL;
        Chrono chrono = new Chrono();
        int number = 10000;
        File file = new File(LocalDateTime.now() + "_" + ScalabilityOfSoda.class + ".csv");
        System.out.println(file.getAbsolutePath());
        try (PrintStream result = new PrintStream(file)) {
            //echaufement
            Set<Problem> problems = CrazyProblemGenerator.generate(50, 10000);
            for (Problem problem : problems) {
                Soda.solve(problem);
            }
            for (int i = 1; i < 100; i++) {
                problems = CrazyProblemGenerator.generate(i, number);
                for (Problem problem : problems) {
                    chrono.start();
                    Soda.solve(problem);
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
