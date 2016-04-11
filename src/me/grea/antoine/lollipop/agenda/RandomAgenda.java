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
import me.grea.antoine.lollipop.type.flaw.Flaw;
import me.grea.antoine.lollipop.type.flaw.Threat;
import me.grea.antoine.utils.Dice;

/**
 *
 * @author antoine
 */
public class RandomAgenda extends Agenda {

    public RandomAgenda(Agenda other) {
        super(other);
    }
    
    public RandomAgenda(Problem problem) {
        super(problem);
    }

    @Override
    protected void populate() {
        for (Action step : problem.plan.vertexSet()) {
            addAll(ClassicalSubGoal.related(step, problem));
        }
        for (Action step : problem.plan.vertexSet()) {
            addAll(Threat.related(step, problem));
        }
    }

    @Override
    public boolean add(Flaw flaw) {
        if(contains(flaw))
            return false;
        return super.add(flaw);
    }
    
    @Override
    public boolean addAll(Collection<? extends Flaw> flaws) {
        boolean modified = false;
        modified = flaws.stream().map((flaw) -> add(flaw)).
                reduce(modified, (accumulator, _item) -> accumulator | _item);
        return modified;
    }

    @Override
    public Flaw choose() {
        Flaw result = Dice.pick(this);
        remove(result);
        return result;
    }

}
