/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package yocto.algorithms;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import yocto.plannification.Action;
import yocto.plannification.Entity;
import yocto.plannification.Goal;
import yocto.plannification.Statement;
import yocto.rdf.NameSpace;
import yocto.utils.Grounder;
import yocto.utils.Log;
import yocto.utils.ModelManager;
import yocto.utils.Query;

/**
 *
 * @author antoine
 */
public class PspMinus {

    private final Model model;
    private final MinPlan minPlan;

    public Map<Entity, Stack> stack;
    public Map<Entity, Map<Goal, List<Action>>> plans;

    public class Stack {

        public Model initial;
        public Deque<Action> observations;

        public Stack(Model initial, Deque<Action> observations) {
            this.initial = Query.named("entities").execute(initial);
            this.observations = observations;
        }

    }

    public PspMinus(Model model, MinPlan minPlan) {
        this.model = model;
        this.minPlan = minPlan;

        stack = new HashMap<Entity, Stack>() {
            {
                put(model.getResource(NameSpace.E + "bob").as(Entity.class), new Stack(model, new ArrayDeque<>()));
            }
        };

        for (Entity agent : stack.keySet()) {
            Model inital = stack.get(agent).initial;
            for (Goal goal : minPlan.goals) {
                Set<Action> pool = new HashSet<>(minPlan.plans.get(goal));
                pool.removeAll(useless(inital, minPlan.plans.get(goal)));
                // In new model before grounding
                planify(pool, inital, goal, new HashSet<Entity>(){{ add(agent);}});

//                psp(plan, inital, goal);
            }
        }

    }

    private Set<Action> useless(Model initial, Set<Action> actions) {
        Set<Action> result = new HashSet<>();
        for (Action action : actions) {
            boolean useless = false;
            Set<Statement> effects = Query.toSet(Query.named("effects").execute(model, new HashMap<String, Node>() {
                {
                    put("a", action.asNode());
                }
            }), "eff", Statement.class);
            for (Statement effect : effects) {
                useless |= effect.isSatisfied(initial);
            }
            if (useless) {
                result.add(action);
            }
        }
        return result;
    }

    private DirectedGraph<Action, DefaultEdge> planify(Set<Action> pool, Model initial, Goal goal, Set<Entity> favorites) {
        Model planModel = ModelManager.create();
        Grounder grounder = new Grounder(initial, favorites);
        for(Action action : pool)
        {
            if(action.isSatisfied(initial))
            {
                grounder.groundAction(action);
                Log.d(action);
                Log.d(action.getPreconditions());
                Log.d(action.getEffects());
            }
        }
        return null;
    }

    private DirectedGraph<Action, DefaultEdge> psp(DirectedGraph<Action, DefaultEdge> partial, Model initial, Goal goal) {
        for (Action action : partial.vertexSet()) {

        }

        return null;
    }

}
