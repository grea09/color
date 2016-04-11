/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.lollipop.type.flaw;

import java.util.HashSet;
import java.util.Set;
import me.grea.antoine.lollipop.type.Action;
import me.grea.antoine.lollipop.type.Problem;
import me.grea.antoine.utils.Log;

/**
 *
 * @author antoine
 */
public class ClassicalResolver extends Resolver {

    public ClassicalResolver(Action source, Action target, int fluent) {
        super(source, target, fluent);
    }

    public ClassicalResolver(Action source, Action target) {
        super(source, target);
    }

    @Override
    public Set<Flaw> related(Problem problem) {
        Set<Flaw> related = new HashSet<>();
        related.addAll(ClassicalSubGoal.related(source, problem));
        for (Action action : problem.plan.vertexSet()) {
            related.addAll(ClassicalThreat.related(action, problem));
        }
        Log.d("Related flaws " + related);

        return related;
    }

}
