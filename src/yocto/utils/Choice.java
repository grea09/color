/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package yocto.utils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author antoine
 */
public class Choice<T> {
    public Set<Task> possibilities = new HashSet<Task>(){

        @Override
        public boolean add(Task e) {
            e.run();
            return super.add(e); //To change body of generated methods, choose Tools | Templates.
        }
        
    };
    
    public Choice(Set<T> choices, final Parallelized<?, T> code)
    {
        for(T choice : choices)
            possibilities.add(new Task(code, choice));
    }
    
}
