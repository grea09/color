/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.soda.test;

import me.grea.antoine.soda.test.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import me.grea.antoine.log.Log;

/**
 *
 * @author antoine
 */
public class Dice {

    static private final Random ENTHROPY = new Random(System.currentTimeMillis()
            * "eohrç(t_àçreapoj€þ€‘’ðþÿêþê€‘’ðÿ|þê€ëê‘’ðüîœûÿþê€ë‘’ðûüfzioh_y('u".hashCode());

    public static int roll(int max) {
        return roll(0, max);
    }

    public static int roll(int min, int max) {
        return ENTHROPY.nextInt((max - min) + 1) + min;
    }
    
    public static Set<Integer> roll(int min, int max, int number)
    {
        assert(max-min>= number);
        Set<Integer> result = new HashSet<>(number);
        while(result.size() < number)
        {
            result.add(roll(min, max));
        }
        return result;
    }
    
    public static Set<Integer> rollNonZero(int min, int max, int number)
    {
        assert(max-min>= number-1);
        Set<Integer> result = new HashSet<>(number);
        while(result.size() < number)
        {
            result.add(roll(min, max));
        }
        result.remove(0);
        return result;
    }
    
    public static <T> Set<T> pick(Set<T> bag, int number)
    {
        assert(number<=bag.size());
        Set<T> picked = new HashSet<>();
        while(picked.size() < number)
        {
            picked.add(pick(bag));
        }
        return picked;
    }
    
    public static <T> T pick(Collection<T> bag)
    {
        int nth = ENTHROPY.nextInt(bag.size());
        for(T picked : bag)
        {
            if(nth-- == 0)
                return picked;
        }
        assert(bag.isEmpty());
        return null;
    }

}
