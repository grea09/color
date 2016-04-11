/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.lollipop.benchmark;

import static java.lang.Integer.max;
import static java.lang.Integer.min;
import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;
import me.grea.antoine.lollipop.mechanism.IllegalFixer;
import me.grea.antoine.lollipop.type.Action;
import me.grea.antoine.lollipop.type.Domain;
import me.grea.antoine.lollipop.type.Edge;
import me.grea.antoine.lollipop.type.Plan;
import me.grea.antoine.lollipop.type.Problem;
import me.grea.antoine.lollipop.type.flaw.ClassicalSubGoal;
import me.grea.antoine.lollipop.type.flaw.ClassicalThreat;
import me.grea.antoine.lollipop.type.flaw.Flaw;
import me.grea.antoine.lollipop.type.flaw.Resolver;
import me.grea.antoine.utils.Collections;
import static me.grea.antoine.utils.Collections.set;
import static me.grea.antoine.utils.Collections.union;
import me.grea.antoine.utils.Dice;
import me.grea.antoine.utils.Log;

/**
 *
 * @author antoine
 */
public class ProblemGenerator {

    public static Set<Problem> generate(int number, int enthropy, int hardness) {
        if (enthropy < 5) {
            Log.f("Enthropy is too low to generate problems !");
        }
        Set<Problem> problems = new HashSet<>(); // Why does it had to be PROBLEMS ?!?!
        problemgen:
        for (int i = 0; i < number; i++) {
            Log.d("i=>" + i);
            Action initial = new Action(null, new HashSet<>()); // 2 outta 10
            Action goal = new Action(Dice.roll(1, enthropy, hardness), null);
            Domain domain = new Domain();
            Problem problem = new Problem(initial, goal, domain, new Plan());

            thunder(problem, enthropy, hardness);
            magInit(problem, enthropy, hardness);
            while (unThreaten(problem, enthropy, hardness) || cycleDestroyer(problem) || magisfy(problem, enthropy, hardness)); //TODO make sure unThreaten and magisfy doesn't generate cycles

            for (Action action : Dice.pick(domain, min(domain.size(), hardness))) {
                domain.add(noise(action, enthropy, hardness));
            }

//            Log.i(problem);
//            if (!SolutionChecker.check(problem)) {
//                Log.f("Generated solution is INVALID !!! \n" + problem);
//            }

            problem.expectedLength = problem.plan.vertexSet().size();
            problem.clear();
            problems.add(problem);
        }
        return problems;
    }
    
    private static boolean magisfy(Problem problem, int enthropy, int hardness)
    {
        Set<Flaw> open = new HashSet<>();
        boolean acted = false;
        do {
            for (Action action : problem.plan.vertexSet()) {
                open.addAll(ClassicalSubGoal.related(action, problem));
            }
            for (Flaw flaw : open) {
                acted = true;
                magiFix(flaw, enthropy, hardness);
            }
            open.clear();
            for (Action action : problem.plan.vertexSet()) {
                open.addAll(ClassicalSubGoal.related(action, problem));
            }
        } while (!open.isEmpty());
        return acted;
    }
    
    private static boolean cycleDestroyer(Problem problem)
    {
        Set<Set<Action>> cycles = new HashSet<>(problem.plan.cycles());
        for (Set<Action> cycle : cycles) {
            for (Action action : new HashSet<>(cycle)) {
                problem.plan.removeVertex(action);
                break;
            }
        }
        return !cycles.isEmpty();
    }

    private static boolean unThreaten(Problem problem, int enthropy, int hardness) {
        Set<Flaw> open = new HashSet<>();
        boolean threatened = false;
        do {
            for (Action action : problem.plan.vertexSet()) {
                open.addAll(ClassicalThreat.related(action, problem));
            }
            for (Flaw flaw : open) {
//                magiFix(flaw, enthropy, hardness);
                threatened = true;
                problem.plan.removeVertex(((ClassicalThreat) flaw).breaker);
                problem.domain.remove(((ClassicalThreat) flaw).breaker);
            }
            open.clear();
            for (Action action : problem.plan.vertexSet()) {
                open.addAll(ClassicalThreat.related(action, problem));
            }
        } while (!open.isEmpty());
        return threatened;
    }

    private static void thunder(Problem problem, int enthropy, int hardness) {
        int size = Dice.roll(1, max(2, problem.goal.preconditions.size() / 2));
        Set<Flaw> open = new HashSet<>();
        for (int i = 0; i < size; i++) {
            for (Action action : problem.plan.vertexSet()) {
                open.addAll(ClassicalSubGoal.related(action, problem));
                open.addAll(ClassicalThreat.related(action, problem));
            }
            for (Flaw flaw : open) {
                magiFix(flaw, enthropy, hardness);
            }
            open.clear();
        }
    }

    private static void magInit(Problem problem, int enthropy, int hardness) {
        Set<Flaw> open = new HashSet<>();
        Set<Integer> initials = new HashSet<>();
        for (Action action : new HashSet<>(problem.plan.vertexSet())) {
            open.addAll(ClassicalSubGoal.related(action, problem));
        }
        for (Flaw flaw : new HashSet<>(open)) {
            if (initials.contains(-flaw.fluent)) {
                Action toAdd = new Action(Dice.pick(initials, Dice.roll(initials.size())), set(flaw.fluent));
                while (problem.plan.reachable(flaw.needer, toAdd)) {
                    toAdd = new Action(Dice.pick(initials, Dice.roll(initials.size())), set(flaw.fluent));
                }
                problem.domain.add(toAdd);
                problem.plan.addVertex(toAdd);
                Edge edge = problem.plan.addEdge(toAdd, flaw.needer);
                edge.labels.add(flaw.fluent);
                open.remove(flaw);
                open.addAll(ClassicalSubGoal.related(toAdd, problem));
            } else {
                initials.add(flaw.fluent);
            }
        }
        problem.initial.effects.addAll(initials);
        problem.plan.addVertex(problem.initial);
        for (Flaw flaw : open) {
            Edge edge = problem.plan.addEdge(problem.initial, flaw.needer);
            if (edge == null) {
                edge = problem.plan.getEdge(problem.initial, flaw.needer);
            }
            edge.labels.add(flaw.fluent);
        }
    }

    private static Action magiFix(Flaw flaw, int enthropy, int hardness) {
        Action added = null;
        for (Resolver resolver : (Collection<Resolver>) flaw.resolvers()) {
            if (resolver.appliable(flaw.problem.plan)) {
                resolver.apply(flaw.problem.plan);
                return null;
            }
        }
        if (flaw instanceof ClassicalSubGoal) {
            added = provider(flaw.problem, enthropy, hardness, flaw.fluent);
            if (flaw.problem.plan.reachable(flaw.needer, added)) {
                magiFix(flaw, enthropy, hardness);
            }
            flaw.problem.domain.add(added);
            flaw.problem.plan.addVertex(added);
            Edge edge = flaw.problem.plan.addEdge(added, flaw.needer);
            edge.labels.add(flaw.fluent);
        } else if (flaw instanceof ClassicalThreat) {
            added = provider(((ClassicalThreat) flaw).problem, enthropy, hardness, -((ClassicalThreat) flaw).fluent);
            added.preconditions.clear();
            if (flaw.problem.plan.reachable(((ClassicalThreat) flaw).needer, added) || flaw.problem.plan.reachable(((ClassicalThreat) flaw).breaker, added)) {
                return magiFix(flaw, enthropy, hardness);
            }
            ((ClassicalThreat) flaw).problem.domain.add(added);
            ((ClassicalThreat) flaw).problem.plan.addVertex(added);
            Edge edge = ((ClassicalThreat) flaw).problem.plan.addEdge(added, ((ClassicalThreat) flaw).needer);
            if (edge == null) {
                edge = flaw.problem.plan.getEdge(added, ((ClassicalThreat) flaw).needer);
            }
            edge.labels.add(-((ClassicalThreat) flaw).fluent);
            ((ClassicalThreat) flaw).threatened.labels.remove(-((ClassicalThreat) flaw).fluent);
            if (((ClassicalThreat) flaw).threatened.labels.isEmpty()) {
                ((ClassicalThreat) flaw).problem.plan.removeEdge(((ClassicalThreat) flaw).threatened);
            }
            ((ClassicalThreat) flaw).problem.plan.addEdge(((ClassicalThreat) flaw).breaker, added);
        }

        return added;
    }

    public static Action provider(Problem problem, int enthropy, int hardness, int fluent) {
        Action provider = new Action();
        provider.effects = roll(enthropy, hardness);
        if (!provider.effects.contains(fluent)) {
            if (!provider.effects.remove(-fluent)) {
                provider.effects.remove(Dice.pick(provider.effects));
            }
            provider.effects.add(fluent);
        }
        provider.preconditions = roll(provider.effects, enthropy, hardness);
        return provider;
    }

    private static Action legal(int enthropy, int hardness) {
        Action legal = new Action();
        legal.effects = roll(enthropy, hardness);
        legal.preconditions = roll(legal.effects, enthropy, hardness);
        return legal;
    }

    private static Set<Integer> roll(int enthropy, int hardness) {
        return roll(new HashSet<>(), enthropy, hardness);
    }

    private static Set<Integer> roll(Set<Integer> forbiden, int enthropy, int hardness) {
        Set<Integer> fluents = new HashSet<>();
        int size = Dice.roll(1, min((enthropy / 2) + hardness, (enthropy * 2) - 1 - forbiden.size()));
        while (fluents.size() != size) {
            boolean negative = hardness == 0 ? false : Dice.chances(enthropy / (double) hardness);
            Set<Integer> rolled = Dice.rollNonZero(negative ? -enthropy : 1, enthropy, size - fluents.size());
            for (Integer fluent : rolled) {
                if (!fluents.contains(-fluent) && !forbiden.contains(fluent)) {
                    fluents.add(fluent);
                }
            }
        }
        return fluents;
    }

    private static Action noise(Action origin, int enthropy, int hardness) {
        Action noisy = new Action(origin);
        int boosted = Dice.roll(1); // 2 choices
        noisy.preconditions.removeAll(Dice.rollNonZero(-enthropy, enthropy, boosted == 0 ? 1 : 0 + Dice.roll(hardness)));
        noisy.preconditions.addAll(Dice.rollNonZero(-enthropy, enthropy, boosted == 0 ? 1 : 0 + Dice.roll(hardness)));
        noisy.effects.removeAll(Dice.rollNonZero(-enthropy, enthropy, boosted == 1 ? 1 : 0 + Dice.roll(hardness)));
        noisy.effects.addAll(Dice.rollNonZero(-enthropy, enthropy, boosted == 1 ? 1 : 0 + Dice.roll(hardness)));
        return noisy;
    }

}
