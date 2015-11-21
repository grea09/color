/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.soda.type.defect;

import java.util.HashSet;
import java.util.Set;
import me.grea.antoine.soda.type.Action;
import me.grea.antoine.soda.type.Edge;
import me.grea.antoine.soda.type.Problem;

/**
 *
 * @author antoine
 */
public class OrphanAction extends Defect {

    public Action orphan;

    public OrphanAction(Action orphan, Problem problem) {
        super(problem);
        this.orphan = orphan;
    }

    @Override
    public void fix() {
        problem.plan.removeVertex(orphan);
    }

    public static Set<OrphanAction> find(Problem problem) {
        Set<OrphanAction> result = new HashSet<>();
        Set<Action> orphan = new HashSet<>();
        int oldSize;
        do {
            oldSize = orphan.size();
            for (Action action : problem.plan.vertexSet()) {
                if (orphan.contains(action))
                    continue;
                
                int toOrphan = 0;
                Set<Edge> outgoing = problem.plan.outgoingEdgesOf(action);
                for(Edge edge : outgoing)
                {
                    toOrphan += orphan.contains(problem.plan.getEdgeTarget(edge)) ? 1 : 0;
                }
                if(outgoing.size() == toOrphan && action != problem.goal && action != problem.initial)
                {
                    orphan.add(action);
                    result.add(new OrphanAction(action, problem));
                }
            }
        } while (oldSize != orphan.size());

        return result;
    }

    @Override
    public String toString() {
        return "Action " + orphan + " is an orphan";
    }

}
