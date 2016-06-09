/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.liris.lollipop2;

import java.io.File;
import java.io.IOException;
import me.grea.antoine.liris.lollipop2.parsing.ProblemParser;
import me.grea.antoine.liris.lollipop2.planning.algorithm.pop.Pop;
import me.grea.antoine.liris.lollipop2.planning.domain.IntFluent;
import me.grea.antoine.liris.lollipop2.planning.problem.Problem;
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
