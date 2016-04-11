/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.lollipop.type.flaw;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;
import me.grea.antoine.lollipop.type.Action;
import me.grea.antoine.lollipop.type.Edge;
import me.grea.antoine.lollipop.type.Plan;
import me.grea.antoine.lollipop.type.Problem;
import me.grea.antoine.utils.Log;

/**
 *
 * @author antoine
 */
public class ClassicalThreat extends Flaw<ClassicalThreat> {

    public Action breaker;
    public Edge threatened;
    private Edge demoter;
    private Edge promoter;

    public ClassicalThreat(Action threat, Edge threatened, int fluent, Problem problem) {
        super(fluent, problem.plan.getEdgeTarget(threatened), problem);
        this.breaker = threat;
        this.threatened = threatened;
    }

    private ClassicalThreat(Problem problem) {
        super(problem);
    }

    @Override
    public Deque<Resolver> resolvers() {
        Deque<Resolver> resolvers = new ArrayDeque<>();
        Action source = problem.plan.getEdgeSource(threatened);
        Action target = problem.plan.getEdgeTarget(threatened);
        if (target != problem.goal) {
            Log.w("Can't demote after goal step !");
            resolvers.add(new ClassicalResolver(target, breaker));
        }
        if (source != problem.initial) {
            Log.w("Can't promote before initial step !");
            resolvers.add(new ClassicalResolver(breaker, source));
        }
        return resolvers;
    }

    public static Set<ClassicalThreat> related(Action troubleMaker, Problem problem) {
        return new ClassicalThreat(problem).related(troubleMaker);
    }

    @Override
    public Set<ClassicalThreat> related(Action troubleMaker) {
        Set<ClassicalThreat> related = new HashSet<>();
        if (troubleMaker != problem.goal && troubleMaker != problem.initial) {
            for (Edge oposite : problem.plan.edgeSet()) {
                for (int fluent : threatens(problem.plan, troubleMaker, oposite)) {
                    related.add(new ClassicalThreat(troubleMaker, oposite, fluent, problem));
                }
            }
        }
        return related;
    }

    public static Set<Integer> threatens(Plan plan, Action breaker, Edge threatened) {
        Set<Integer> fluents = new HashSet<>();
        breaker.effects.stream().filter((fluent) -> (threatened.labels.contains(-fluent))). //NEVER have a 0 effect
                forEach((fluent) -> {
                    Action source = plan.getEdgeSource(threatened);
                    Action target = plan.getEdgeTarget(threatened);
                    if (breaker != source && breaker != target && !plan.reachable(breaker, source)
                            && !plan.reachable(target, breaker)) {
                        fluents.add(fluent);
                    }
                });
        return fluents;
    }

    @Override
    public String toString() {
        return breaker + " ☠ " + threatened;
    }

}
