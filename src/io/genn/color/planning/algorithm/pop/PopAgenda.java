/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.genn.color.planning.algorithm.pop;

import io.genn.color.planning.algorithm.pop.flaws.PopSubGoal;
import io.genn.color.planning.algorithm.pop.flaws.PopThreat;
import java.util.ArrayList;
import io.genn.color.planning.algorithm.Agenda;
import io.genn.color.planning.algorithm.Flaw;
import io.genn.color.planning.algorithm.Resolver;
import io.genn.color.planning.domain.Problem;
import me.grea.antoine.utils.random.Dice;

/**
 *
 * @author antoine
 */
public class PopAgenda extends Agenda {

    public PopAgenda(Agenda other) {
        super(other);
    }

    public PopAgenda(Problem problem) {
        super(problem);
    }

    @Override
    protected void populate() {
        addAll(new PopSubGoal(null, null, problem).flaws());
        addAll(new PopThreat(null, null, null, problem).flaws());
    }

    @Override
    public Flaw choose() {
        Flaw result = Dice.pick(this); //Be random
        remove(result);
        return result;
    }

    @Override
    public void related(Resolver resolver) {
        for (Flaw flaw : new ArrayList<>(this)) {
            if (flaw.invalidated(resolver)) {
                remove(flaw);
            }
        }
        addAll(new PopSubGoal(null, null, problem).related(resolver));
        addAll(new PopThreat(null, null, null, problem).related(resolver));
    }

}
