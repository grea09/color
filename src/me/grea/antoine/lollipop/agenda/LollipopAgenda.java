/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.lollipop.agenda;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import me.grea.antoine.lollipop.type.Action;
import me.grea.antoine.lollipop.type.Edge;
import me.grea.antoine.lollipop.type.Problem;
import me.grea.antoine.lollipop.type.flaw.Alternative;
import me.grea.antoine.lollipop.type.flaw.Flaw;
import me.grea.antoine.lollipop.type.flaw.Orphan;
import me.grea.antoine.lollipop.type.flaw.SubGoal;
import me.grea.antoine.lollipop.type.flaw.Threat;
import me.grea.antoine.utils.Collections;

/**
 *
 * @author antoine
 */
public class LollipopAgenda extends Agenda {

    public LollipopAgenda(Problem problem) {
        super(problem);
    }

    @Override
    protected void populate() {
        for (Map.Entry<Action, Action> entry : problem.plan.updated.entrySet()) {
            Set<Integer> preconditions = Collections.difference(entry.getKey().preconditions, entry.getValue().preconditions); //Removed preconditions
            Set<Integer> effects = Collections.difference(entry.getValue().effects, entry.getKey().effects); // Added effects
            for (Integer effect : effects) {
                for (Action action : problem.providing.get(effect)) {
                    if (action != entry.getValue()) {
                        addAll(Alternative.related(action, problem));
                    }
                }
            }
            for (Edge edge : new HashSet<>(problem.plan.incomingEdgesOf(entry.getValue()))) {
                if (!edge.labels.isEmpty() && edge.labels.removeAll(preconditions) && edge.labels.isEmpty()) {
                     problem.plan.removeEdge(edge);
                }
            }
        }

        for (Action step : problem.plan.vertexSet()) { // Probably slow
//            addAll(Alternative.related(step, problem));
            addAll(SubGoal.related(step, problem));
            addAll(Threat.related(step, problem));
//            addAll(Orphan.related(step, problem));
        }
    }

    @Override
    public boolean addAll(Collection<? extends Flaw> related) {
        related.removeAll(this);
        return super.addAll(0, related);
    }
//        if (flaw instanceof Threat) { //Fix associated subgoals before !
//            Deque<Flaw> top = new ArrayDeque<>();
//
//            Threat threat = (Threat) flaw;
//            Action provider = problem.plan.getEdgeSource(threat.threatened);
//            for (Flaw subgoal : agenda) {
//                if (flaw instanceof ClassicalSubGoal) {
//                    if (((ClassicalSubGoal) flaw).needer.equals(provider) || ((ClassicalSubGoal) flaw).needer.equals(threat.needer)) {
//                        top.addFirst(flaw);
//                    } else if (((ClassicalSubGoal) flaw).needer.equals(threat.breaker)) {
//                        top.addLast(flaw);
//                    }
//                }
//            }
//            agenda.removeAll(top);
//            top.addLast(flaw);
//            flaw = top.pop();
//            while (!top.isEmpty()) {
//                agenda.addFirst(top.removeLast());
//            }
//        }

//        boolean result = false;
//        for (Flaw newFlaw : related) {
//            result = true;
//            if (!contains(newFlaw)) {
//                if (newFlaw instanceof Orphan) {
//                    addLast(newFlaw);
//                    Set<Flaw> uselessSubGoals = new HashSet<>();
//                    for (Flaw uselessSubGoal : this) {
//                        if (uselessSubGoal instanceof ClassicalSubGoal && uselessSubGoal.needer.equals(((Orphan) newFlaw).needer)) {
//                            uselessSubGoals.add(uselessSubGoal);
//                        }
//                    }
//                    removeAll(uselessSubGoals);
//                    continue;
//                }
//                addFirst(newFlaw); // To quickly find problems with this choice
//            }
//        }
//        return result;
//    }
    @Override
    public boolean add(Flaw flaw) {
        if (contains(flaw)) {
            return false;
        }
        return super.add(flaw);
    }

    @Override
    public Flaw choose() {
        Flaw result = get(0);
        remove(0);
        return result;
    }

}
