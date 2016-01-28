/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.lollipop.algorithm;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;
import me.grea.antoine.utils.Log;
import me.grea.antoine.lollipop.exception.Success;
import me.grea.antoine.lollipop.type.Action;
import me.grea.antoine.lollipop.type.PartialSolution;
import me.grea.antoine.lollipop.type.Plan;
import me.grea.antoine.lollipop.type.flaw.Flaw;
import me.grea.antoine.lollipop.type.Problem;
import me.grea.antoine.lollipop.type.flaw.Alternative;
import me.grea.antoine.lollipop.type.flaw.Orphan;
import me.grea.antoine.lollipop.type.flaw.Resolver;
import me.grea.antoine.lollipop.type.flaw.SubGoal;
import me.grea.antoine.lollipop.type.flaw.Threat;

/**
 *
 * @author antoine
 */
public class PartialOrderOptimizedPlanning {

    private final Problem problem;
    private final Deque<Flaw> agenda = new ArrayDeque<>();
    private final Deque<PartialSolution> partialSolutions = new ArrayDeque<>();

    public PartialOrderOptimizedPlanning(Problem problem) {
        this.problem = problem;
        IllegalFixer.clean(problem);
        populateAgenda();
    }

    private void populateAgenda() {
        agenda.addAll(Alternative.find(problem));
        agenda.addAll(SubGoal.find(problem));
        agenda.addAll(Threat.find(problem));
        agenda.addAll(Orphan.find(problem));
    }

    public static void solve(Problem problem) {
        PartialOrderOptimizedPlanning pop = new PartialOrderOptimizedPlanning(problem);
        while (true) {
            try {
                pop.refine();
            } catch (Success ex) {
                Log.i("Success !");
                return;
            }
            Log.w("Failure");
            pop.soft();
        }
    }

    public void refine() throws Success {
        if (agenda.isEmpty()) {
            throw new Success();
        }
        Log.d("Agenda " + agenda);
        Flaw flaw = agenda.pop();

        if (flaw instanceof Threat) { //Fix associated subgoals before !
            Deque<Flaw> top = new ArrayDeque<>();

            Threat threat = (Threat) flaw;
            Action provider = problem.plan.getEdgeSource(threat.threatened);
            for (Flaw subgoal : agenda) {
                if (flaw instanceof SubGoal) {
                    if (((SubGoal) flaw).needer.equals(provider) || ((SubGoal) flaw).needer.equals(threat.needer)) {
                        top.addFirst(flaw);
                    } else if (((SubGoal) flaw).needer.equals(threat.breaker)) {
                        top.addLast(flaw);
                    }
                }
            }
            agenda.removeAll(top);
            top.addLast(flaw);
            flaw = top.pop();
            while (!top.isEmpty()) {
                agenda.addFirst(top.removeLast());
            }
        }
        Log.d("Resolving " + flaw);
        Deque<Resolver> resolvers = flaw.resolvers();
        Log.d("Resolvers " + resolvers);
        for (Resolver resolver : resolvers) {
            Log.d("Trying with " + resolver);

            if (resolver.appliable(problem.plan)) {
                resolver.apply(problem.plan);
            } else {
                Log.w(resolver + " isn't appliable !");
                partialSolutions.push(new PartialSolution(problem.plan, flaw, agenda));
                continue;
            }

            Set<Flaw> related = resolver.related(problem);
            Log.d("Related " + related);
            for (Flaw newFlaw : related) {
                if (!agenda.contains(newFlaw)) {
                    if (newFlaw instanceof Orphan) {
                        agenda.addLast(newFlaw);
                        Set<Flaw> uselessSubGoals = new HashSet<>();
                        for (Flaw uselessSubGoal : agenda) {
                            if (uselessSubGoal instanceof SubGoal && uselessSubGoal.needer.equals(((Orphan) newFlaw).needer)) {
                                uselessSubGoals.add(uselessSubGoal);
                            }
                        }
                        agenda.removeAll(uselessSubGoals);
                        continue;
                    }
                    agenda.addFirst(newFlaw); // To quickly find problems with this choice
                }
            }
            refine();
            resolver.revert(problem.plan);
            partialSolutions.push(new PartialSolution(problem.plan, flaw, agenda));
        }
        Log.w("No suitable resolver for " + flaw);
        partialSolutions.push(new PartialSolution(problem.plan, flaw, agenda));
    }

    private void soft() {
        double minViolation = Double.POSITIVE_INFINITY; // double for infinity
        Plan best = problem.plan;
        Flaw annoyer = null;
        while (!partialSolutions.isEmpty()) {
            PartialSolution partialSolution = partialSolutions.removeLast();
            double currentViolation = partialSolution.remaining.size() + (partialSolution.plan.violation());
            if (currentViolation < minViolation) {
                best = partialSolution.plan;
                annoyer = partialSolution.cause;
                minViolation = currentViolation;
            }
        }
        problem.plan = best;
        Log.i("Violation : " + Math.round(minViolation));
        for (Resolver resolver : annoyer.healers()) {
            resolver.apply(best);
        };
        Log.i("God, " + annoyer + " was annoying !");
    }

}
