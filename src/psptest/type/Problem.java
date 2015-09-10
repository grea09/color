/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package psptest.type;

import psptest.type.Goal;
import psptest.type.Edge;
import psptest.type.Action;
import java.util.Set;
import org.jgrapht.DirectedGraph;

/**
 *
 * @author antoine
 */
public class Problem {
    public Action initial;
    public Action goal;
    public Set<Action> actions; // not including those above
    public DirectedGraph<Action, Edge> plan;

    public Problem(Action initial, Action goal, Set<Action> actions, DirectedGraph<Action, Edge> plan) {
        this.initial = initial;
        this.goal = goal;
        this.actions = actions;
        this.plan = plan;
    }

    @Override
    public String toString() {
        return "Problem {" + "\n\tinitial:" + initial.effects + "=> goal:" + ((Goal)goal).toString() + "\n\tactions:" + actions + "\n\tplan:" + planToString() + "}";
    }
    
    public String planToString()
    {
        String result = "";
        for(Edge edge : plan.edgeSet())
        {
            result += plan.getEdgeSource(edge) + " => " + plan.getEdgeTarget(edge) +"\n\t\t";
        }
        return result;
    }
    
    
    
}
