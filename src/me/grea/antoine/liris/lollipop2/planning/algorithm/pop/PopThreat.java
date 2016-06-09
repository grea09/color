/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.liris.lollipop2.planning.algorithm.pop;

import static com.google.common.collect.Sets.union;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import me.grea.antoine.liris.lollipop2.planning.domain.Action;
import me.grea.antoine.liris.lollipop2.planning.domain.Fluent;
import me.grea.antoine.liris.lollipop2.planning.flaw.Flaw;
import me.grea.antoine.liris.lollipop2.planning.flaw.Resolver;
import me.grea.antoine.liris.lollipop2.planning.problem.CausalLink;
import me.grea.antoine.liris.lollipop2.planning.problem.Problem;

/**
 *
 * @author antoine
 * @param <F>
 */
public class PopThreat<F extends Fluent> extends Flaw<F> {

    private final CausalLink<F> threatened;
    private final Action<F> breaker;

    public PopThreat(Problem<F> problem) {
        this(null, null, null, problem);
    }

    public PopThreat(F fluent, Action<F> breaker, CausalLink<F> threatened, Problem<F> problem) {
        super(fluent, threatened == null ? null : threatened.target(), problem);
        this.breaker = breaker;
        this.threatened = threatened;
    }

    @Override
    public Deque<Resolver<F>> resolvers() {
        Deque<Resolver<F>> resolvers = new ArrayDeque<>();
        if (threatened.target() != problem.goal) {
//            Log.w("Can't demote after goal step !");
            resolvers.add(new Resolver<>(threatened.target(), breaker));
        }
        if (threatened.source() != problem.initial) {
//            Log.w("Can't promote before initial step !");
            resolvers.add(new Resolver<>(breaker, threatened.source()));
        }
        return resolvers;
    }

    @Override
    public Set<PopThreat<F>> related(Resolver<F> resolver) {
        return union(related(problem.plan.edge(resolver.source, resolver.target)),
                related(resolver.source));
    }

    public Set<PopThreat<F>> related(CausalLink<F> fragile) {
        Set<PopThreat<F>> related = new HashSet<>();
        for (Action<F> action : problem.plan.vertexSet()) {
            if (action != problem.initial && action != problem.goal) {
                related.addAll(related(fragile, action));
            }
        }
        return related;
    }

    public Set<PopThreat<F>> related(Action<F> breaker) {
        Set<PopThreat<F>> related = new HashSet<>();
        for (CausalLink<F> link : problem.plan.edgeSet()) {
            related.addAll(related(link, breaker));
        }
        return related;
    }

    public Set<PopThreat<F>> related(CausalLink<F> fragile, Action<F> breaker) {
        Set<PopThreat<F>> related = new HashSet<>();
        for (F cause : fragile.causes) {
            if (breaker.eff.contradicts(cause) && breaker != fragile.source() && breaker != fragile.target()
                    && (!problem.plan.reachable(fragile.target(), breaker)
                    && !problem.plan.reachable(breaker, fragile.source()))) {
                related.add(new PopThreat<>(cause, breaker, fragile, problem));
            }
        }
        return related;
    }

    @Override
    public Set<PopThreat<F>> flaws() {
        Set<PopThreat<F>> flaws = new HashSet<>();
        for (CausalLink<F> link : problem.plan.edgeSet()) {
            flaws.addAll(related(link));
        }
        return flaws;
    }

    @Override
    public boolean invalidated(Resolver<F> resolver) {
        return (problem.plan.reachable(threatened.target(), resolver.source) && problem.plan.reachable(resolver.target, breaker))
                || (problem.plan.reachable(breaker, resolver.source) && problem.plan.reachable(resolver.target, threatened.source())); //FIXME Handle negative for LOLLIPOP
    }
    
    @Override
    public String toString() {
        return "<threatened " + threatened + ", breaker " + breaker + ">";
    }

}
