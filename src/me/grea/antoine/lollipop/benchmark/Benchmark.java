/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.lollipop.benchmark;

import me.grea.antoine.utils.Log;

/**
 *
 * @author antoine
 */
public class Benchmark {
    
    static {
        Log.level = Log.Level.INFO;
    }
    
    public static void main(String[] args)
    {
        Log.i(ProblemGenerator.generate(1000, 50));
    }
    
}
