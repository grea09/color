/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.lollipop.mechanism;

import static java.lang.Math.abs;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import me.grea.antoine.lollipop.type.Action;
import me.grea.antoine.lollipop.type.Domain;
import me.grea.antoine.lollipop.type.Edge;
import me.grea.antoine.lollipop.type.Problem;
import static java.lang.Math.abs;
import static java.lang.Math.pow;
import static java.lang.Math.round;
import java.util.Arrays;
import static java.lang.Math.abs;
import static java.lang.Math.abs;
import static java.lang.Math.round;

/**
 *
 * @author antoine
 */
public class Ranking implements Comparator<Action> {

    public static final double ALPHA = 4;
    public static final double[] PARAM_PLUS = {1.0};
    public static final double[] PARAM_MINUS = {1.0, 2.0, 4.0, 3.0};

    private Domain domain;
    private Problem problem;
    public Map<Action, Double> ranks;
    private Map<Action, Double[]> scores;
    private Map<Action, Double[]> bonuses;
    private Map<Integer, Set<Action>> unappliables;
    private Map<Integer, Set<Action>> eagers;

    public Ranking(Domain domain) {
        this.domain = domain;
        this.unappliables = unappliables();
        this.scores = scores();
        this.eagers = eagers();
        this.bonuses = new HashMap<>();
        this.ranks = new HashMap<>();
    }

    public Ranking(Ranking other) {
        this.domain = other.domain;
        this.unappliables = new HashMap<>(other.unappliables);
        this.scores = new HashMap<>(other.scores);
        this.eagers = new HashMap<>(other.eagers);
        this.bonuses = new HashMap<>(other.bonuses);
        this.ranks = new HashMap<>(other.ranks);
    }

    public void realize(Problem problem) {
        for (int fluent : problem.initial.effects) {
            Set<Action> unappliable = unappliables.get(fluent);
            if (unappliable == null) {
                continue;
            }

            for (Action action : unappliable) {
                boost(action, -1);
            }
        }
        for (int fluent : problem.goal.preconditions) {
            Set<Action> eager = eagers.get(fluent);
            if (eager == null) {
                continue;
            }
            for (Action action : eager) {
                boost(action, 1);
            }
        }
        ranks.put(problem.initial, Double.POSITIVE_INFINITY);
        ranks.put(problem.goal, Double.POSITIVE_INFINITY);

        for (Action action : problem.domain) {
            if (action.fake) {
                ranks.put(action, 0.0);
            } else {
                Double[] score = scores.get(action);
                Double[] bonus = bonuses.get(action);
                if (bonus == null) {
                    bonus = new Double[2];
                    Arrays.fill(bonus, 0.0);
                }
                ranks.put(action, score[0] + bonus[0] * pow(ALPHA, -(score[1] + bonus[1]))); // POWOWW
            }
        }

    }

    private void boost(Action action, int boost) {
        Double[] bonus = bonuses.get(action);
        if (bonus == null) {
            bonus = new Double[2];
            Arrays.fill(bonus, 0.0);
            bonuses.put(action, bonus);
        }
        bonus[boost > 0 ? 0 : 1] += boost > 0 ? PARAM_PLUS[0] : PARAM_MINUS[3];
    }

    private Map<Action, Double[]> scores() {
        Map<Action, Double[]> scores = new HashMap<>();
        for (Action action : domain) {
            double good = PARAM_PLUS[0] * domain.properPlan.outDegreeOf(action);
            double bad = PARAM_MINUS[0] * action.preconditions.size();
            for (Set<Action> cycle : domain.properPlan.cycles) {
                if (cycle.contains(action)) {
                    bad += PARAM_MINUS[1] * cycle.size();
                }
            }
            Edge selfEdge = domain.properPlan.getEdge(action, action);
            if (selfEdge != null) {
                bad += PARAM_MINUS[2] * selfEdge.labels.size();
            }
            scores.put(action, new Double[]{good, bad});
        }
        for (Set<Action> value : unappliables.values()) {
            for (Action action : value) {
                scores.get(action)[1] += PARAM_MINUS[3];
            }
        }
        return scores;
    }

    private Map<Integer, Set<Action>> unappliables() {
        Map<Integer, Set<Action>> unappliable = new HashMap<>();
        for (Action action : domain) {
            Set<Integer> nonSatisfied = new HashSet<>(action.preconditions);
            domain.properPlan.incomingEdgesOf(action).stream().forEach((edge) -> {
                nonSatisfied.removeAll(edge.labels);
            });
            for (Integer fluent : nonSatisfied) {
                Set<Action> get = unappliable.get(fluent);
                if (get == null) {
                    get = new HashSet<>();
                    unappliable.put(fluent, get);
                }
                get.add(action);
            }
        }
        return unappliable;
    }

    private Map<Integer, Set<Action>> eagers() {
        Map<Integer, Set<Action>> eagers = new HashMap<>();
        for (Action action : domain) {
            Set<Integer> nonUsed = new HashSet<>(action.effects);
            domain.properPlan.outgoingEdgesOf(action).stream().forEach((edge) -> {
                nonUsed.removeAll(edge.labels);
            });
            for (Integer fluent : nonUsed) {
                Set<Action> get = eagers.get(fluent);
                if (get == null) {
                    get = new HashSet<>();
                    eagers.put(fluent, get);
                }
                get.add(action);
            }
        }
        return eagers;
    }

    public double h(Action action) {
        Double rank = ranks.get(action);
        if (rank == null) {
            return Double.NaN;
        }
        return rank;
    }

    @Override
    public int compare(Action o1, Action o2) {
        return ((Long) round(h(o1) - h(o2))).intValue();
    }

}
