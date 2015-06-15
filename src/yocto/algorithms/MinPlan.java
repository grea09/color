/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package yocto.algorithms;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import yocto.utils.Log;
import yocto.utils.Query;

/**
 *
 * @author antoine
 */
public class MinPlan {

    public Map<Resource, Set<Resource>> plans;
    private final Model model;
    public final Set<Resource> goals;

    public MinPlan(Model model) {
        this.model = model;

        goals = Query.toSet(Query.named("goals").execute(model), "g");

        plans = new HashMap<>();
        for (Resource goal : goals) {
            Set<Resource> actions = minPlan(goal.asNode());
            Log.i("Goal :" + goal + "\n" + actions);
            plans.put(goal, actions);
        }
    }

    @Override
    public String toString() {

        String result = "";
        for (Resource goal : plans.keySet()) {
            result += "Goal :" + goal + " Plan: ";
            for (Resource action : plans.get(goal)) {
                result += action + " | ";
            }
            result += "\n";
        }
        return result;
    }

    private Set<Resource> minPlan(Node goal) {
        Set<Resource> actions = actionsSatisfying(goal);
        Deque<Resource> open = new ArrayDeque<>(actions);
        while (!open.isEmpty()) {
            Resource action = open.pop();
            Set<Resource> candidates = actionsSatisfying(action.asNode());
            for (Resource candidate : candidates) {
                if (!actions.contains(candidate)) {
                    open.push(candidate);
                }
            }
            actions.addAll(candidates);

        }
        return actions;
    }

    private Set<Resource> actionsSatisfying(Node referee) {
        return Query.toSet(Query.named().execute(model,
                new HashMap<String, Node>() {
                    {
                        put("x", referee);
                    }
                }), "a");
    }

}
