/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.lollipop.mechanism;

import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import me.grea.antoine.lollipop.type.Action;
import me.grea.antoine.lollipop.type.Domain;
import me.grea.antoine.lollipop.type.Edge;
import me.grea.antoine.lollipop.type.Plan;
import me.grea.antoine.lollipop.type.Problem;
import me.grea.antoine.utils.Collections;
import static me.grea.antoine.utils.Collections.list;
import static me.grea.antoine.utils.Collections.set;
import static me.grea.antoine.utils.Collections.queue;

/**
 *
 * @author antoine
 */
public class ProperPlan extends Plan {

    private Problem problem;
    private Domain domain;
    public final Map<Integer, List<Action>> providing = new HashMap<>();
    public final Map<Integer, List<Action>> needing = new HashMap<>();
    public final Set<Set<Action>> cycles;
    private boolean binding = false;

    public ProperPlan(ProperPlan other) {
        super();
        this.cycles = new HashSet<>(other.cycles);
        this.providing.putAll(other.providing);
        this.needing.putAll(other.needing);
        this.domain = other.domain;
        this.problem = other.problem;
        for (Action action : other.vertexSet()) {
            addVertex(action);
        }
        for (Edge edge : other.edgeSet()) {
            addEdge(other.getEdgeSource(edge), other.getEdgeTarget(edge), edge);
        }
        binding = true;
    }

    public ProperPlan(Domain domain) {
        binding = true;
        this.domain = domain;
        addAllVertex(domain);
        this.cycles = cycles();
    }

    public ProperPlan(Problem problem) {
        this(problem.domain.properPlan);
        this.problem = problem;
        binding = true;
        addVertex(problem.goal);
        addVertex(problem.initial);
    }

    @Override
    public boolean removeVertex(Action v) {
        providing.values().stream().forEach((value) -> {
            value.remove(v);
        });
        needing.values().stream().forEach((value) -> {
            value.remove(v);
        });
        return super.removeVertex(v); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean addVertex(Action action) {
        boolean modified = super.addVertex(action);
        if (modified) {
            cache(action);
            if (binding) {
                bind(action);
            }
        }
        return modified;
    }

    public static void sanic(Problem problem) {
        // Gotta go fast
        Deque<Action> open = Collections.queue(problem.goal);
        Deque<Action> closed = Collections.queue();
        while (!open.isEmpty()) {
            Action current = open.pop();
            closed.push(current);
            for (int precondition : current.preconditions) {
                List<Action> providers = problem.providing.get(precondition);
                if (providers == null || providers.isEmpty()) {
                    problem.plan.removeVertex(current);
                    continue;
                }
                Action next = providers.get(0);
                if (closed.contains(next)) {
                    continue;
                }
                if (problem.plan.addVertex(next)) {
                    open.push(next);
                }
                Edge edge = problem.plan.addEdge(next, current);
                edge.labels.add(precondition);
            }
        }
    }

    private void thunderPrunning() {
        Set<Action> used = set(problem.initial, problem.goal);
        Deque<Action> forward = queue(problem.initial);
        Deque<Action> backward = queue(problem.goal);
        while (!forward.isEmpty() && !backward.isEmpty()) {
            Set<Edge> forwardEdges = outgoingEdgesOf(forward.pop());
            Set<Edge> backwardEdges = incomingEdgesOf(backward.pop());

        }
    }

    public void cache(Collection<Action> actions) {
        for (Action action : actions) {
            cache(action);
        }
    }

    public void cache(Action action) {
        for (int effect : action.effects) {
            if (providing.containsKey(effect)) {
                List<Action> providers = providing.get(effect);
                if (!providers.contains(action)) {
                    providers.add(action);
                }
            } else {
                providing.put(effect, list(action));
            }
        }
        for (int precondition : action.preconditions) {
            if (needing.containsKey(precondition)) {
                List<Action> needers = needing.get(precondition);
                if (!needers.contains(action)) {
                    needers.add(action);
                }
            } else {
                needing.put(precondition, list(action));
            }
        }
    }

    private void bind(Action action) {
        for (int precondition : action.preconditions) {
            if (providing.containsKey(precondition)) {
                for (Action source : providing.get(precondition)) {
                    Edge edge = getEdge(source, action);
                    if (edge == null) {
                        edge = addEdge(source, action);
                    }
                    edge.labels.add(precondition);
                }
            }
        }
        for (int effect : action.effects) {
            if (needing.containsKey(effect)) {
                for (Action target : needing.get(effect)) {
                    Edge edge = getEdge(action, target);
                    if (edge == null) {
                        edge = addEdge(action, target);
                    }
                    edge.labels.add(effect);
                }
            }
        }
    }

}
