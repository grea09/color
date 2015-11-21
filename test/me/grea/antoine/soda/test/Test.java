/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.soda.test;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.grea.antoine.log.Log;
import me.grea.antoine.soda.algorithm.PartialOrderPlanning;
import me.grea.antoine.soda.algorithm.Soda;
import me.grea.antoine.soda.exception.Failure;
import me.grea.antoine.soda.exception.Success;
import me.grea.antoine.soda.type.Problem;

/**
 *
 * @author antoine
 */
public class Test {
    
    public static void main(String[] args) throws Failure
    {
        Set<Problem> problems = ValidProblemGenerator.generate(100, 20);
        for(Problem problem : problems)
        {
            PartialOrderPlanning.solve(problem);
        }
    }
    
}
