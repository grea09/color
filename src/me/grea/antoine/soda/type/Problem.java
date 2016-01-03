/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.soda.type;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author antoine
 */
public class Problem {

    public Action initial;
    public Action goal;
    public Set<Action> actions; // not including those above
    public Plan plan;

    public Problem() {
        this(new Action(), new Action(), new HashSet<>(), new Plan());
    }

    public Problem(Action initial, Action goal, Set<Action> actions, Plan plan) {
        this.initial = initial;
        this.goal = goal;
        this.actions = actions;
        this.plan = plan;
        plan.addVertex(initial);
        plan.addVertex(goal);
    }

    public Problem(Problem other) {
        this.initial = other.initial;
        this.goal = other.goal;
        this.actions = other.actions;
        this.plan = other.plan;
    }

    @Override
    public String toString() {
        return "Problem {" + "\n\tinitial:" + initial.effects + " => goal:" + ((Goal) goal).toString() + "\n\tactions:" + actions + "\n\tplan:" + planToString() + "}";
    }

    public String planToString() {
//        return plan.edgeSet().toString();
        String result = "";
        for (Edge edge : plan.edgeSet()) {
            result += edge + "\n\t";
        }
        return result;
    }

}
