/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.lollipop.agenda;

import me.grea.antoine.lollipop.type.Action;
import me.grea.antoine.lollipop.type.Problem;
import me.grea.antoine.lollipop.type.flaw.Alternative;
import me.grea.antoine.lollipop.type.flaw.ClassicalSubGoal;
import me.grea.antoine.lollipop.type.flaw.Cycle;
import me.grea.antoine.lollipop.type.flaw.Flaw;
import me.grea.antoine.lollipop.type.flaw.Orphan;
import me.grea.antoine.lollipop.type.flaw.SubGoal;
import me.grea.antoine.lollipop.type.flaw.Threat;

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
        for (Action step : problem.plan.vertexSet()) { // Probably slow
            addAll(Alternative.related(step, problem));
        }
        for (Action step : problem.plan.vertexSet()) {
            addAll(SubGoal.related(step, problem));
        }
        for (Action step : problem.plan.vertexSet()) {
            addAll(Threat.related(step, problem));
        }
        for (Action step : problem.plan.vertexSet()) {
            addAll(Orphan.related(step, problem));
        }
    }
    
//    @Override
//    public boolean addAll(Collection<? extends Flaw> related) {
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
    
//    @Override
//    public boolean add(Flaw flaw) {
//        super.add(flaw);
//    }

    @Override
    public Flaw choose() {
        Flaw result = get(0);
        remove(0);
        return result;
    }

}
