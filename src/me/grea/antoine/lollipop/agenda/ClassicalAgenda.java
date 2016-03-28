/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.lollipop.agenda;

import java.util.Collection;
import me.grea.antoine.lollipop.type.Action;
import me.grea.antoine.lollipop.type.Problem;
import me.grea.antoine.lollipop.type.flaw.ClassicalSubGoal;
import me.grea.antoine.lollipop.type.flaw.ClassicalThreat;
import me.grea.antoine.lollipop.type.flaw.Flaw;
import me.grea.antoine.lollipop.type.flaw.Threat;

/**
 *
 * @author antoine
 */
public class ClassicalAgenda extends Agenda {

    public ClassicalAgenda(Problem problem) {
        super(problem);
    }

    @Override
    protected void populate() {
        for (Action step : problem.plan.vertexSet()) {
            addAll(ClassicalSubGoal.related(step, problem));
        }
        for (Action step : problem.plan.vertexSet()) {
            addAll(ClassicalThreat.related(step, problem));
        }
    }

    @Override
    public boolean add(Flaw flaw) {
        super.add(0,flaw);
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends Flaw> c) {
        for (Flaw flaw : c) {
            add(flaw);
        }
        return c.size() > 0;
    }

    @Override
    public Flaw choose() {
        Flaw result = get(0);
        remove(0);
        return result;
    }

}
