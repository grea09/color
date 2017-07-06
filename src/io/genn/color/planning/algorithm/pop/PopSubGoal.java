/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.genn.color.planning.algorithm.pop;

import io.genn.color.planning.domain.Action;
import io.genn.color.planning.domain.Fluent;
import io.genn.color.planning.domain.State;
import io.genn.color.planning.flaw.Flaw;
import io.genn.color.planning.flaw.Resolver;
import io.genn.color.planning.problem.CausalLink;
import io.genn.color.planning.problem.Problem;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author antoine
 */
public class PopSubGoal<F extends Fluent> extends Flaw<F> {

    public PopSubGoal(Problem<F> problem) {
        super(null, null, problem);
    }

    public PopSubGoal(F fluent, Action<F> needer, Problem<F> problem) {
        super(fluent, needer, problem);
    }

    @Override
    public Deque<Resolver<F>> resolvers() {
        Deque<Resolver<F>> resolvers = new ArrayDeque<>();
        if (problem.initial.eff.unifies(fluent)) {
            resolvers.add(new Resolver<>(problem.initial, needer, fluent));
        }
        for (Action<F> step : problem.plan.vertexSet()) {
            if (step.eff.unifies(fluent)) {
                resolvers.add(new Resolver<>(step, needer, fluent));
            }
        }
        for (Action<F> action : problem.domain) {
            if (action.eff.unifies(fluent)) {
                resolvers.add(new Resolver<>(action, needer, fluent));
            }
        }
        return resolvers;
    }

    @Override
    public Set<PopSubGoal<F>> related(Resolver<F> resolver) {
        return related(resolver.source);
    }

    public Set<PopSubGoal<F>> related(Action<F> annoyer) {
        State<F> open = new State<>(annoyer.pre, false);
        if (problem.plan.containsVertex(annoyer)) {
            for (CausalLink<F> link : problem.plan.incomingEdgesOf(annoyer)) {
                open.removeAll(link.causes);
            }
        }
        else {
            return new HashSet<>();
        }
        Set<PopSubGoal<F>> related = new HashSet<>();
        for (F f : open) {
            related.add(new PopSubGoal<>(f, annoyer, problem));
        }
        return related;
    }

    @Override
    public Set<PopSubGoal<F>> flaws() {
        Set<PopSubGoal<F>> flaws = new HashSet<>();
        for (Action<F> action : problem.plan.vertexSet()) {
            flaws.addAll(related(action));
        }
        return flaws;
    }

    @Override
    public boolean invalidated(Resolver<F> resolver) {
        return resolver.target == needer && resolver.fluent != null && fluent.unifies(resolver.fluent);//FIXME In Lollipop we need negative too
    }

    @Override
    public String toString() {
        return "<needer " + needer + ", fluent " + fluent + ">";
    }
    
    

}
