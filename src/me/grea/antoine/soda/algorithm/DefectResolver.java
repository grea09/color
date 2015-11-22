/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.soda.algorithm;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import me.grea.antoine.log.Log;
import me.grea.antoine.soda.type.Problem;
import me.grea.antoine.soda.type.defect.CompetingAction;
import me.grea.antoine.soda.type.defect.CompetingLink;
import me.grea.antoine.soda.type.defect.Cycle;
import me.grea.antoine.soda.type.defect.Defect;
import me.grea.antoine.soda.type.defect.InconsistentAction;
import me.grea.antoine.soda.type.defect.LiarLink;
import me.grea.antoine.soda.type.defect.RedundantLink;
import me.grea.antoine.soda.type.defect.ToxicAction;
import me.grea.antoine.soda.type.defect.OrphanAction;
import static me.grea.antoine.soda.utils.Collections.concat;

/**
 *
 * @author antoine
 */
public class DefectResolver {

    public static void clean(Problem problem) {
        assert (problem.initial.preconditions.isEmpty());
        assert (problem.goal.effects.isEmpty());
        problem.plan.addVertex(problem.initial);
        problem.plan.addVertex(problem.goal);

        fix(find(problem), problem);

        assert (count(problem) == 0); //FIXME : slow
    }

    public static int count(Problem problem) {
        return find(problem).size();
    }

    public static List<Defect> find(Problem problem) {
        return concat(illegal(problem), interfering(problem));
    }

    private static void fix(Collection<Defect> defects, Problem problem) {
        for (Defect defect : defects) {
//            Log.d("[×] " + defect);
            defect.fix();
        }
    }

    private static List<Defect> illegal(Problem problem) {
        return concat(
                Cycle.find(problem),
                InconsistentAction.find(problem),
                ToxicAction.find(problem),
                LiarLink.find(problem));
    }

    private static List<Defect> interfering(Problem problem) {
        return concat(RedundantLink.find(problem),
                CompetingLink.find(problem),
                CompetingAction.find(problem),
                OrphanAction.find(problem));
    }

}
