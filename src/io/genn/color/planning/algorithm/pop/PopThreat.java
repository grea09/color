/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.genn.color.planning.algorithm.pop;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;
import io.genn.color.planning.domain.Action;
import io.genn.color.planning.domain.Fluent;
import io.genn.color.planning.domain.State;
import io.genn.color.planning.flaw.Flaw;
import io.genn.color.planning.flaw.Resolver;
import io.genn.color.planning.problem.CausalLink;
import io.genn.color.planning.problem.Problem;
import static me.grea.antoine.utils.collection.Collections.union;

/**
 *
 * @author antoine
 */
public class PopThreat extends Flaw {

    private final CausalLink threatened;
    private final Action breaker;

    public PopThreat(Problem problem) {
        this(null, null, null, problem);
    }

    public <F extends Fluent<F, ?>> PopThreat(F fluent, Action<F, ?> breaker, CausalLink threatened, Problem problem) {
        super(fluent, threatened == null ? null : threatened.target(), problem);
        this.breaker = breaker;
        this.threatened = threatened;
    }

    @Override
    public Deque<Resolver> resolvers() {
        Deque<Resolver> resolvers = new ArrayDeque<>();
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
    public Set<PopThreat> related(Resolver resolver) {
        return union(related(problem.plan.edge(resolver.source, resolver.target)),
                related(resolver.source));
    }

    public Set<PopThreat> related(CausalLink fragile) {
        Set<PopThreat> related = new HashSet<>();
        for (Action action : problem.plan.vertexSet()) {
            if (action != problem.initial && action != problem.goal) {
                related.addAll(related(fragile, action));
            }
        }
        return related;
    }

    public Set<PopThreat> related(Action breaker) {
        Set<PopThreat> related = new HashSet<>();
        for (CausalLink link : problem.plan.edgeSet()) {
            related.addAll(related(link, breaker));
        }
        return related;
    }

    public <F extends Fluent<F, ?>> Set<PopThreat> related(CausalLink fragile, Action<F, ?> breaker) {
        Set<PopThreat> related = new HashSet<>();
        for (F cause : (State<F>) fragile.causes) {
            if (breaker.eff.contradicts(cause) && breaker != fragile.source() && breaker != fragile.target()
                    && (!problem.plan.reachable(fragile.target(), breaker)
                    && !problem.plan.reachable(breaker, fragile.source()))) {
                related.add(new PopThreat(cause, breaker, fragile, problem));
            }
        }
        return related;
    }

    @Override
    public Set<PopThreat> flaws() {
        Set<PopThreat> flaws = new HashSet<>();
        for (CausalLink link : problem.plan.edgeSet()) {
            flaws.addAll(related(link));
        }
        return flaws;
    }

    @Override
    public boolean invalidated(Resolver resolver) {
        return (problem.plan.reachable(threatened.target(), resolver.source) && problem.plan.reachable(resolver.target, breaker))
                || (problem.plan.reachable(breaker, resolver.source) && problem.plan.reachable(resolver.target, threatened.source())); //FIXME Handle negative for LOLLIPOP
    }
    
    @Override
    public String toString() {
        return threatened + " =/= " + breaker;
    }

}
