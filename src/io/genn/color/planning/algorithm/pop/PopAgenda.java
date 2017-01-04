/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.genn.color.planning.algorithm.pop;

import java.util.ArrayList;
import java.util.Set;
import io.genn.color.planning.domain.Fluent;
import io.genn.color.planning.flaw.Agenda;
import io.genn.color.planning.flaw.Flaw;
import io.genn.color.planning.flaw.Resolver;
import io.genn.color.planning.problem.Problem;
import me.grea.antoine.utils.Dice;

/**
 *
 * @author antoine
 */
public class PopAgenda<F extends Fluent> extends Agenda<F> {

    public PopAgenda(Agenda other) {
        super(other);
    }

    public PopAgenda(Problem problem) {
        super(problem);
    }

    @Override
    protected void populate() {
        addAll(new PopSubGoal<>(null, null, problem).flaws());
        addAll(new PopThreat<>(null, null, null, problem).flaws());
    }

    @Override
    public Flaw<F> choose() {
        Flaw<F> result = Dice.pick(this); //Be random
        remove(result);
        return result;
    }

    @Override
    public void related(Resolver<F> resolver) {
        for (Flaw<F> flaw : new ArrayList<>(this)) {
            if (flaw.invalidated(resolver)) {
                remove(flaw);
            }
        }
        addAll(new PopSubGoal<>(null, null, problem).related(resolver));
        addAll(new PopThreat<>(null, null, null, problem).related(resolver));
    }

}
