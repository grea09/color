/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.lollipop.benchmark;

import java.util.Deque;
import java.util.HashSet;
import java.util.Set;
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
        assert (enthropy > 3);
        Set<Problem> problems = new HashSet<>(); // Why does it had to be PROBLEMS ?!?!
        problemgen:
        for (int i = 0; i < number; i++) {
            Action initial = new Action(null, Dice.roll(1, enthropy, enthropy / 5)); // 2 outta 10
            Action goal = new Action(set(), null);
            Domain domain = new Domain();
            Problem problem = new Problem(initial, goal, domain, new Plan());

            generateUsefull(problem, enthropy, hardness);

            for (Action action : Dice.pick(domain, hardness)) {
                domain.add(noise(action, enthropy));
            }

            for (int j = 0; j < enthropy /3; j++) {
                 Set<Integer> effects = Dice.pick(problem.plan.vertexSet()).effects;
                if (!effects.isEmpty()) {
                    goal.preconditions.addAll(Dice.pick(effects, Dice.roll(1, 1 + (enthropy / 6))));
                }
            }

            insert(goal, problem);
            problem.plan.addVertex(goal); // Whyyyyyy
            Set<Integer> satisfied = new HashSet<>();
            for (Edge edge : problem.plan.incomingEdgesOf(goal)) {
                satisfied.addAll(edge.labels);
            }

            for (Integer fluent : Collections.difference(goal.preconditions, satisfied)) {
                generateProvider(problem, enthropy, fluent);
            }

//            Log.i(problem.planToString());
            assert (SolutionChecker.check(problem));
            
            problem.expectedLength = problem.plan.vertexSet().size();

            problem.plan = new Plan();
            problem.plan.addVertex(initial);
            problem.plan.addVertex(goal);
            problem.domain = new Domain(domain);
            
            problems.add(problem);
        }
        return problems;
    }

    private static void generateProvider(Problem problem, int enthropy, int fluent) {
        Action action = positive(problem.initial.effects, enthropy);
        action.effects.add(fluent);

        if (!insert(action, problem)) {
            generateProvider(problem, enthropy, fluent);
        }
    }

    private static void generateUsefull(Problem problem, int enthropy, int hardness) {
        Set<Integer> state = new HashSet<>(problem.initial.effects);
        for (int j = 0; j < enthropy; j++) {
            Action action;
            if (Dice.chances((float)hardness/enthropy)) {
                action = negative(state, enthropy);
            } else {
                action = positive(state, enthropy);
            }

            if (!insert(action, problem)) {
                j--;
                continue;
            }

            state.addAll(action.effects);
        }
        Set<Flaw> open = new HashSet<>();
        do {
            for (Action action : new HashSet<>(problem.plan.vertexSet())) {
                try {
                    open.addAll(ClassicalSubGoal.related(action, problem));
                    open.addAll(ClassicalThreat.related(action, problem));
                } catch (IllegalArgumentException e) {
                    problem.plan.addVertex(action); // Bug of jgrapht
                    open.addAll(ClassicalSubGoal.related(action, problem));
                    open.addAll(ClassicalThreat.related(action, problem));
                }
            }
            for (Flaw flaw : new HashSet<>(open)) {
                boolean resolved = false;
                for (Resolver resolver : (Deque<? extends Resolver>) flaw.resolvers()) {
                    if (!resolver.appliable(problem.plan)) {
                        continue;
                    }
                    resolver.apply(problem.plan);
                    resolved = true;
                    break;
                }
                if (resolved) {
                    open.remove(flaw);
                }
            }

        } while (!open.isEmpty());

    }

    private static boolean insert(Action action, Problem problem) {
        problem.domain.add(action);
        problem.plan.addVertex(action);
        for (ClassicalSubGoal subgoal : ClassicalSubGoal.related(action, problem)) {
            Resolver resolver = subgoal.resolvers().pop();
            if (resolver.appliable(problem.plan)) {
                resolver.apply(problem.plan);
            } else {
                action.preconditions.remove(resolver.fluent);
            }
        }
        for (ClassicalThreat threat : ClassicalThreat.related(action, problem)) {
            Resolver resolver = threat.resolvers().pop();
            if (resolver.appliable(problem.plan)) {
                resolver.apply(problem.plan);
            } else {
                problem.domain.remove(action);
                problem.plan.removeVertex(action);
                return false;
            }
        }
        return true;
    }

    private static Action negative(Set<Integer> state, int enthropy) {
        return new Action(Dice.pick(state, Dice.roll(enthropy / 5)), union(Dice.roll(1, enthropy, Dice.roll(enthropy / 10)), Dice.roll(enthropy, -1, Dice.roll(enthropy / 5))));
    }

    private static Action positive(Set<Integer> state, int enthropy) {
        return new Action(Dice.pick(state, Dice.roll(enthropy / 5)), Dice.roll(1, enthropy, Dice.roll(enthropy / 4)));
    }

    private static Action noise(Action origin, int enthropy) {
        Action noisy = new Action(origin);
        noisy.preconditions.removeAll(Dice.rollNonZero(-enthropy, enthropy, Dice.roll(enthropy / 10)));
        noisy.preconditions.addAll(Dice.rollNonZero(-enthropy, enthropy, Dice.roll(enthropy / 10)));
        noisy.effects.removeAll(Dice.rollNonZero(-enthropy, enthropy, Dice.roll(enthropy / 8)));
        noisy.effects.addAll(Dice.rollNonZero(-enthropy, enthropy, Dice.roll(enthropy / 8)));
        return noisy;
    }

}
