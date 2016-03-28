/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.lollipop.heuristic;

import static java.lang.Math.round;
import java.util.Comparator;
import me.grea.antoine.lollipop.type.Action;
import me.grea.antoine.lollipop.type.Problem;

/**
 *
 * @author antoine
 */
public abstract class Heuristic {
    
    protected final Problem problem;

    public Heuristic(Problem problem) {
        this.problem = problem;
    }
    
    public abstract double h(Action action);
    
    public Comparator<Action> comparator() {
        return (Action a1, Action a2) -> { 
            if (a1.equals(a2)) {
                return 0;
            }
            boolean a1Special = problem.isSpecial(a1);
            boolean a2Special = problem.isSpecial(a2);
            if(a1Special && a2Special)
                return 0;
            else if (a1Special)
                return 1;
            else if (a2Special)
                return -1;
            return cast(h(a1)-h(a2));
        };
    }
    
    protected static int cast(double l) {
        if (l > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        } else if (l < Integer.MIN_VALUE) {
            return Integer.MIN_VALUE;
        }
        return (int) round(l);
    }
    
    
    
}
