/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package yocto.algorithms;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ReifiedStatement;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jgrapht.DirectedGraph;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import yocto.rdf.NameSpace;
import yocto.rdf.Predicate;
import yocto.utils.Comparator;
import yocto.utils.Query;

/**
 *
 * @author antoine
 */
public class PspMinus {

    private final Model model;
    private final MinPlan minPlan;

    public Map<Resource, Stack> stack;
    public Map<Resource, Map<Resource, List<Resource>>> plans;

    public class Stack {

        public Model initial;
        public Deque<Resource> observations;

        public Stack(Model initial, Deque<Resource> observations) {
            this.initial = Query.named("entities").execute(initial);
            this.observations = observations;
        }

    }

    public PspMinus(Model model, MinPlan minPlan) {
        this.model = model;
        this.minPlan = minPlan;

        stack = new HashMap<Resource, Stack>() {
            {
                put(model.getResource(NameSpace.E + "bob"), new Stack(model, new ArrayDeque<>()));
            }
        };

        for (Resource agent : stack.keySet()) {
            Model inital = stack.get(agent).initial;
            for (Resource goal : minPlan.goals) {
                Set<Resource> pool = new HashSet<>(minPlan.plans.get(goal));
                pool.removeAll(useless(inital, minPlan.plans.get(goal)));
                // In new model before grounding
                Model ground = ModelFactory.createMemModelMaker().createDefaultModel();
                ground.add(goal.listProperties());
                goal = ground.getResource(goal.getURI());
                DefaultDirectedGraph<Resource, DefaultEdge> plan = new DefaultDirectedGraph<Resource, DefaultEdge>(DefaultEdge.class) {
                    {
                        for (Resource action : pool) {
                            ground.add(action.listProperties());
                            addVertex(ground.getResource(action.getURI()));
                        }
                    }
                };
                plan.addVertex(goal);
                psp(plan, inital, goal);
            }
        }

    }

    private Set<Resource> useless(Model initial, Set<Resource> actions) {
        Set<Resource> result = new HashSet<>();
        for (Resource action : actions) {
            boolean useless = false;
            Set<Resource> effects = Query.toSet(Query.named("effects").execute(model, new HashMap<String, Node>() {
                {
                    put("a", action.asNode());
                }
            }), "eff");
            for (Resource effect : effects) {
                useless |= Comparator.satisfied(effect.as(ReifiedStatement.class).getStatement(), initial);
            }
            if (useless) {
                result.add(action);
            }
        }
        return result;
    }

    private DirectedGraph<Resource, DefaultEdge> psp(DirectedGraph<Resource, DefaultEdge> partial, Model initial, Resource goal) {

        return null;
    }

}
