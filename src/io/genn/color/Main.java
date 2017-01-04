/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.genn.color;

import java.io.File;
import java.io.IOException;
import io.genn.color.parsing.ProblemParser;
import io.genn.color.planning.algorithm.pop.Pop;
import io.genn.color.planning.domain.IntFluent;
import io.genn.color.planning.problem.Problem;
import me.grea.antoine.utils.Log;

/**
 *
 * @author antoine
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        // TODO code application logic here
        Problem<IntFluent> problem = ProblemParser.parse(new File("data/sample.w"));
        Pop pop = new Pop(problem);
        pop.solve();
        Log.i(problem);
    }
    
}
