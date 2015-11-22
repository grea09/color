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
import me.grea.antoine.soda.test.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.grea.antoine.log.Log;
import me.grea.antoine.soda.algorithm.DefectResolver;
import me.grea.antoine.soda.algorithm.PartialOrderPlanning;
import me.grea.antoine.soda.algorithm.Soda;
import me.grea.antoine.soda.exception.Failure;
import me.grea.antoine.soda.type.Plan;
import me.grea.antoine.soda.type.Problem;
import me.grea.antoine.soda.type.defect.CompetingAction;
import me.grea.antoine.soda.type.defect.CompetingLink;
import me.grea.antoine.soda.type.defect.Cycle;
import me.grea.antoine.soda.type.defect.Defect;
import me.grea.antoine.soda.type.defect.InconsistentAction;
import me.grea.antoine.soda.type.defect.LiarLink;
import me.grea.antoine.soda.type.defect.OrphanAction;
import me.grea.antoine.soda.type.defect.RedundantLink;
import me.grea.antoine.soda.type.defect.ToxicAction;
import me.grea.antoine.soda.utils.Collections.*;
import static me.grea.antoine.soda.utils.Collections.union;
import me.grea.antoine.soda.utils.Stats;

/**
 *
 * @author antoine
 */
public class QualityOfPop {

    public static void main(String[] args) throws Failure {
        Log.level = Log.Level.FATAL;
        Chrono popChrono = new Chrono();
        Chrono sodaChrono = new Chrono();
        int number = 10000;
        File file = new File(LocalDateTime.now() + "_" + QualityOfPop.class + ".csv");
        System.out.println(file.getAbsolutePath());
        try (PrintStream result = new PrintStream(file)) {
            for (int i = 2; i < 100; i++) {
                System.gc();
                Set<Problem> problems = ValidProblemGenerator.generate(i, number);
                List<Double> actionQuality = new ArrayList<>();
                List<Double> edgeQuality = new ArrayList<>();
                for (Problem problem : problems) {
                    popChrono.start();
                    PartialOrderPlanning.solve(problem);
                    popChrono.stop();
                    actionQuality.add(qualityAction(problem));
                    edgeQuality.add(qualityEdge(problem));
                    problem.plan = new Plan();
                    sodaChrono.start();
                    Soda.solve(problem);
                    sodaChrono.stop();
                }
                System.out.println(i + " => " + popChrono);
                result.println(i + "," + 
                        popChrono.total / number + ", " + 
                        sodaChrono.total / number + ", " + 
                        Stats.average(actionQuality) + ", " + 
                        Stats.average(edgeQuality));
                popChrono.reset();
                sodaChrono.reset();
            }
            result.close();
        } catch (FileNotFoundException ex) {
            Log.e(ex);
        }

    }

    private static Double qualityAction(Problem problem) {
        return 1.0 - ((double) union(
                InconsistentAction.find(problem),
                ToxicAction.find(problem),
                OrphanAction.find(problem)).size()
                / problem.plan.vertexSet().size());
    }

    private static Double qualityEdge(Problem problem) {
        return 1.0 - ((double) union(
                CompetingAction.find(problem),
                CompetingLink.find(problem),
                Cycle.find(problem),
                LiarLink.find(problem),
                RedundantLink.find(problem)).size()
                / problem.plan.edgeSet().size());
    }

}
