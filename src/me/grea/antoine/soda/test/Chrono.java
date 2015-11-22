/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.soda.test;

/**
 *
 * @author antoine
 */
public class Chrono {
    
    public long begin;
    public long total = 0;

    public Chrono() {
    }
    
    public void start() {
        begin = System.nanoTime();
    }
    
    public long stop() {
        long elapsed = System.nanoTime() - begin;
        total += elapsed;
        return elapsed;
    }
    
    public void reset() {
        total = 0;
    }

    @Override
    public String toString() {
        return "" + (total /1000000000.0);
    }
    
    
    
}
