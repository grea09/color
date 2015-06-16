/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package yocto.algorithms;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import yocto.plannification.Action;
import yocto.plannification.Goal;
import yocto.utils.Log;
import yocto.utils.Query;

/**
 *
 * @author antoine
 */
public class MinPlan {

    public Map<Goal, Set<Action>> plans;
    private final Model model;
    public final Set<Goal> goals;

    public MinPlan(Model model) {
        this.model = model;

        goals = Query.toSet(Query.named("goals").execute(model), "g", Goal.class);

        plans = new HashMap<>();
        for (Goal goal : goals) {
            Set<Action> actions = minPlan(goal);
            Log.i("Goal " + goal + "\n" + actions);
            plans.put(goal, actions);
        }
    }

    @Override
    public String toString() {
        String result ="";
        for (Map.Entry<Goal, Set<Action>> entry : plans.entrySet()) {
            result += "Goal " + entry.getKey() + "\n" + entry.getValue();
        }
        return result;
    }

    private Set<Action> minPlan(Goal goal) {
        Set<Action> actions = actionsSatisfying(goal);
        Deque<Action> open = new ArrayDeque<>(actions);
        while (!open.isEmpty()) {
            Action action = open.pop();
            Set<Action> candidates = actionsSatisfying(action);
            for (Action candidate : candidates) {
                if (!actions.contains(candidate)) {
                    open.push(candidate);
                }
            }
            actions.addAll(candidates);

        }
        return actions;
    }

    private Set<Action> actionsSatisfying(Goal referee) {
        return Query.toSet(Query.named().execute(model,
                new HashMap<String, Node>() {
                    {
                        put("x", referee.asNode());
                    }
                }), "a", Action.class);
    }

}
