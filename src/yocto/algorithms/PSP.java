/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package yocto.algorithms;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import yocto.plannification.Action;
import yocto.plannification.Entity;
import yocto.plannification.Goal;
import yocto.plannification.Statement;
import yocto.rdf.NameSpace;
import yocto.utils.Grounder;
import me.grea.antoine.utils.Log;
import yocto.utils.ModelManager;
import yocto.utils.Query;

/**
 *
 * @author antoine
 */
public class PSP {

    private final Model model;
    private final MinPlan minPlan;

    public Map<Entity, Stack> stack;
    public Map<Entity, Map<Goal, List<Action>>> plans;

    private static class Fail extends Exception { // FAIL !

        public Fail() {
        }
    }

    public class Stack {

        public Model initial;
        public Deque<Action> observations;

        public Stack(Model initial, Deque<Action> observations) {
            this.initial = Query.named("entities").execute(initial);
            this.observations = observations;
        }

    }

    public PSP(Model model, MinPlan minPlan) {
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
                Log.d("Plan for " + goal);
                Set<Action> pool = new HashSet<>(minPlan.plans.get(goal));
                pool.removeAll(useless(inital, minPlan.plans.get(goal)));
                Log.d("useful pool :" + pool);
            }
        }

    }

    private Set<Action> useless(Model initial, Set<Action> actions) {
        Set<Action> result = new HashSet<>();
        for (Action action : actions) {
            boolean useless = true;
            Set<Statement> effects = action.getEffects();
            for (Statement effect : effects) {
                if (!effect.isSatisfied(initial)) {
                    useless = false;
                    break;
                }
            }
            if (useless) {
                result.add(action);
            }
        }
        return result;
    }

    private DirectedGraph<Action, DefaultEdge> psp(DirectedGraph<Action, DefaultEdge> partial, Model initial, Goal goal) {
        Set<Statement> preconditions = new HashSet<Statement>() {
            {
                addAll(goal.getPreconditions());
                for (Action step : partial.vertexSet()) {
                    addAll(step.getPreconditions());
                }
            }
        };
        Set<Statement> unsatisfied = preconditions;
        Set<Statement> satisfied = new HashSet<>();

        for (Statement precondition : unsatisfied) {
            try {
                // search in initial state
                chooseInitial(initial, precondition);
                // search in plan
                for (Action step : partial.vertexSet()) {
                    for (Statement effect : step.getEffects()) {
                        if(precondition.compatible(effect).satisfies())
                        {
                            
                        }
                    }
                }
                // search in pool
                // search in actions
            }
            // for(Statement effect : effects) {
            //if(effects.satisfies(precondition)
            catch (Fail ex) {
                Log.d(ex);
                
            }
        }

        return null;
    }
    
    private void chooseInitial(Model initial, Statement precondition) throws Fail
    {
        if (! precondition.isSatisfied(initial)) 
            throw new Fail();
    }

}
