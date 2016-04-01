/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.lollipop.type;

import java.util.List;
import java.util.Map;
import me.grea.antoine.lollipop.mechanism.Ranking;

/**
 *
 * @author antoine
 */
public class Problem {

    public Action initial;
    public Action goal;
    public Domain domain; // not including those above
    public Plan plan;
    public Map<Integer, List<Action>> providing;
    public int expectedLength = 0;
    public Ranking ranking;

    public Problem() {
        this(new Action(), new Action(), new Domain(), new Plan());
    }

    public Problem(Action initial, Action goal, Domain domain, Plan plan) {
        this.initial = initial;
        this.goal = goal;
        this.domain = domain;
        this.plan = plan;
        plan.addVertex(initial);
        plan.addVertex(goal);
    }

    public Problem(Problem other) {
        this.initial = other.initial;
        this.goal = other.goal;
        this.domain = other.domain;
        this.plan = other.plan;
    }
    
    @Override
    public String toString() {
        return "Problem {" + "\n\tdomain:" + domain + "\n\tinitial:" + initial.effects + " => goal:" + goal.preconditions + "\n\tplan:" + planToString() + "}";
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
