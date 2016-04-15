/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.lollipop.benchmark;

import static java.lang.Integer.max;
import static java.lang.Integer.min;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.grea.antoine.lollipop.algorithm.Lollipop;
import me.grea.antoine.lollipop.algorithm.PartialOrderPlanning;
import me.grea.antoine.lollipop.type.Action;
import me.grea.antoine.lollipop.type.Domain;
import me.grea.antoine.lollipop.type.Edge;
import me.grea.antoine.lollipop.type.Plan;
import me.grea.antoine.lollipop.type.Problem;
import me.grea.antoine.lollipop.type.flaw.ClassicalSubGoal;
import me.grea.antoine.lollipop.type.flaw.ClassicalThreat;
import me.grea.antoine.lollipop.type.flaw.Flaw;
import me.grea.antoine.lollipop.type.flaw.Resolver;
import static me.grea.antoine.utils.Collections.set;
import static me.grea.antoine.utils.Collections.union;
import me.grea.antoine.utils.Dice;
import me.grea.antoine.utils.Log;

/**
 *
 * @author antoine
 */
public class ProblemGenerator {

    public static final int YANAMAR = 10;
    private static int yanamar = 10; // Variable CRUCIALE !!!

    public static Set<Problem> generate(int number, int enthropy, int hardness) {
        if (enthropy < 5) {
            Log.f("Enthropy is too low to generate problems !");
        }
        Set<Problem> problems = new HashSet<>(); // Why does it had to be PROBLEMS ?!?!
        problemgen:
        for (int i = 0; i < number; i++) {
            Log.i("i=>" + i);
            Action initial = new Action(null, new HashSet<>()); // 2 outta 10
            Action goal = new Action(Dice.roll(1, enthropy, max(1, min(Dice.roll(1, enthropy / 2), hardness))), null);
            Domain domain = new Domain();
            Problem problem = new Problem(initial, goal, domain, new Plan());

            thunder(problem, enthropy, hardness);
            magInit(problem, enthropy, hardness);
            fixer(problem, enthropy, hardness);
            if (yanamar == 0) {
                i--;
                yanamar = YANAMAR;
                continue;
            }

            for (Action action : Dice.pick(domain, min(domain.size(), hardness))) {
                domain.add(noise(action, enthropy, hardness));
            }

//            Log.i(problem);
            yanamar = YANAMAR;
            if (!SolutionChecker.check(problem)) {
                Log.e("Generated solution is INVALID !!! \n" + problem);
                i--;

                continue;
            }
            problem.expectedLength = problem.plan.vertexSet().size();
            problem.clear();
            try {
                if (!Executors.newSingleThreadExecutor().submit(() -> {
                    return Lollipop.solve(problem);
                }).get(1200, TimeUnit.MILLISECONDS)) {
                    Log.e("POP didn't make it !!! \n" + problem);
                    i--;
                    continue;
                }
            } catch (ExecutionException | InterruptedException | TimeoutException ex) {
                Log.e("POP took too long !!! \n");
                i--;
                continue;
            }
            problem.clear();

            problems.add(problem);
        }
        return problems;
    }

    private static void fixer(Problem problem, int enthropy, int hardness) {
        Set<Flaw> open = new HashSet<>();
        do {
            yanamar--;
            for (Action action : problem.plan.vertexSet()) {
                open.addAll(ClassicalSubGoal.related(action, problem));
                open.addAll(ClassicalThreat.related(action, problem));
            }
            for (Flaw flaw : open) {
                Action action = realFix(flaw, enthropy, hardness);
            }
            cycleDestroyer(problem);
            open.clear();
            for (Action action : problem.plan.vertexSet()) {
                open.addAll(ClassicalSubGoal.related(action, problem));
                open.addAll(ClassicalThreat.related(action, problem));
            }
        } while (!open.isEmpty() && yanamar > 0);
    }

    private static boolean cycleDestroyer(Problem problem) {
        Set<Set<Action>> cycles = new HashSet<>(problem.plan.cycles());
        for (Set<Action> cycle : cycles) {
            for (Action action : new HashSet<>(cycle)) {
                problem.plan.removeVertex(action);
                problem.domain.remove(action);
            }
        }
        return !cycles.isEmpty();
    }

    private static void thunder(Problem problem, int enthropy, int hardness) {
        int size = Dice.roll(1, max(2, problem.goal.preconditions.size() / 2));
        Set<Flaw> open = new HashSet<>();
        Set<Integer> threatening = new HashSet<>();
        for (int i = 0; i < size; i++) {
            for (Action action : problem.plan.vertexSet()) {
                open.addAll(ClassicalSubGoal.related(action, problem));
                open.addAll(ClassicalThreat.related(action, problem));
            }
            for (Flaw flaw : open) {
                threatening.add(-flaw.fluent);
                Action action = magiFix(flaw, threatening, enthropy, hardness);
                if (action != null) {
                    for (Integer effect : action.effects) {
                        threatening.add(-effect);
                    }
                }
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

    private static Action realFix(Flaw flaw, int enthropy, int hardness) {
        Action added = null;
        Log.d("Flaw :" + flaw);
        if (flaw instanceof ClassicalSubGoal) {
            added = new Action(set(), set(((ClassicalSubGoal) flaw).fluent));
            flaw.problem.domain.add(added);
            flaw.problem.plan.addVertex(added);
            flaw.problem.plan.addEdge(added, flaw.needer);
            Edge edge = flaw.problem.plan.getEdge(added, flaw.needer);
            edge.labels.add(flaw.fluent);
            Log.d("Added :" + edge);
        } else if (flaw instanceof ClassicalThreat) {
            ((ClassicalThreat) flaw).threatened.labels.remove(-((ClassicalThreat) flaw).fluent);
            if (((ClassicalThreat) flaw).threatened.labels.isEmpty()) {
                Log.d("Removing weak link.");
                ((ClassicalThreat) flaw).problem.plan.removeEdge(((ClassicalThreat) flaw).threatened);
            }
            if (((ClassicalThreat) flaw).needer == ((ClassicalThreat) flaw).problem.goal) {
                added = new Action(set(), set(-((ClassicalThreat) flaw).fluent));
                Log.d("White knight :" + added);
                if (!flaw.problem.plan.containsEdge(added, ((ClassicalThreat) flaw).needer)) {
                    ((ClassicalThreat) flaw).problem.domain.add(added);
                    ((ClassicalThreat) flaw).problem.plan.addVertex(added);
                    Edge edge = flaw.problem.plan.addEdge(added, flaw.needer);
                    if (edge != null) {
                        edge.labels.add(-((ClassicalThreat) flaw).fluent);
                    }
                    Log.d("Replacing weak link with :" + edge);
                }

                Edge edge = ((ClassicalThreat) flaw).problem.plan.addEdge(((ClassicalThreat) flaw).breaker, added);
                Log.d("Adding the anti threat link : " + edge);

                Action relaxed = new Action(((ClassicalThreat) flaw).breaker);
                relaxed.effects.remove(((ClassicalThreat) flaw).fluent);
                ((ClassicalThreat) flaw).problem.plan.update(flaw.needer, relaxed);
                for (Edge liar : new HashSet<>(((ClassicalThreat) flaw).problem.plan.outgoingEdgesOf(relaxed))) {
                    if (liar.labels.remove(((ClassicalThreat) flaw).fluent) && liar.labels.isEmpty()) {
                        ((ClassicalThreat) flaw).problem.plan.removeEdge(liar);
                    }
                }

            } else {
                Action relaxed = new Action(((ClassicalThreat) flaw).needer);
                relaxed.preconditions.remove(-((ClassicalThreat) flaw).fluent);
                ((ClassicalThreat) flaw).problem.plan.update(flaw.needer, relaxed);
            }
        }

        return added;
    }

    private static Action magiFix(Flaw flaw, Set<Integer> threatening, int enthropy, int hardness) {
        Action added = null;
        Log.d("Flaw :" + flaw);
        for (Resolver resolver : (Collection<Resolver>) flaw.resolvers()) {
            if (resolver.appliable(flaw.problem.plan)) {
                Log.d("Flaw is resolvable with " + resolver);
                resolver.apply(flaw.problem.plan);
                return null;
            }
        }
        if (flaw instanceof ClassicalSubGoal) {
            added = provider(flaw.problem, threatening, enthropy, hardness, flaw.fluent);
            while (((ClassicalSubGoal) flaw).problem.plan.containsVertex(added)) {
                added = provider(flaw.problem, threatening, enthropy, hardness, flaw.fluent);
            }
            flaw.problem.domain.add(added);
            flaw.problem.plan.addVertex(added);
            Edge edge = flaw.problem.plan.addEdge(added, flaw.needer);
            edge.labels.add(flaw.fluent);
            Log.d("Added :" + edge);
        } else if (flaw instanceof ClassicalThreat) {
            realFix(flaw, enthropy, hardness);
        }

        return added;
    }

    public static Action provider(Problem problem, Set<Integer> threatening, int enthropy, int hardness, int fluent) {
        Action provider = new Action();
        provider.effects = roll(threatening, enthropy, hardness);
        if (!provider.effects.contains(fluent)) {
            provider.effects.remove(-fluent);
            provider.effects.add(fluent);
        }
        provider.preconditions = roll(union(threatening, provider.effects), enthropy, hardness);
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
        if (forbiden.size() == enthropy * 2) {
            return fluents;
        }
        int size = Dice.roll(0, min((enthropy / 3) + hardness, (enthropy * 2) - forbiden.size()));
        for (int i = 0; i < size; i++) {
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
