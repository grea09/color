/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package soda.exception;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import soda.type.Action;
import soda.type.Edge;
import soda.type.Problem;
import soda.type.Problem.Flaw;

/**
 *
 * @author antoine
 */
public class Failure extends Exception {
    public Flaw cause;
    public Problem partialSolution;

    public Failure(Problem partialSolution, Flaw cause) {
        this.cause = cause;
        partialSolution.partialSolutions.put(cause, (DirectedGraph<Action, Edge>) ((DefaultDirectedGraph<Action, Edge>) partialSolution.plan).clone());
        this.partialSolution = partialSolution;
    }

    @Override
    public String getMessage() {
        return "Failure caused by " + cause;
    }
    
    
    
}
