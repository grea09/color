/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.soda.utils;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import jdk.nashorn.internal.runtime.arrays.ArrayLikeIterator;

/**
 *
 * @author
 * http://www.java2s.com/Code/Java/Collections-Data-Structure/Setoperationsunionintersectiondifferencesymmetricdifferenceissubsetissuperset.htm
 */
public class Collections {

    public static <T> List<T> concat(Collection<? extends T>... collections) {
        List<T> tmp = new ArrayList<>();
        for (Collection<? extends T> collection : collections) {
            tmp.addAll(collection);
        }
        return tmp;
    }
    
    public static <T> Set<T> union(Set<? extends T>... sets) {
        Set<T> tmp = new HashSet<>();
        for (Set<? extends T> set : sets) {
            tmp.addAll(set);
        }
        return tmp;
    }

    public static <T> Set<T> intersection(Set<? extends T>... sets) {
        Set<T> tmp = new HashSet<>(sets[0]);
        for (Set<? extends T> set : sets) {
            tmp.retainAll(set);
        }
        return tmp;
    }

    public static <T> Set<T> difference(Set<? extends T> setA, Set<? extends T> setB) {
        Set<T> tmp = new HashSet<>(setA);
        tmp.removeAll(setB);
        return tmp;
    }

    public static <T> Set<T> symDifference(Set<? extends T> setA, Set<? extends T> setB) {
        Set<T> tmpA;
        Set<T> tmpB;

        tmpA = union(setA, setB);
        tmpB = intersection(setA, setB);
        return difference(tmpA, tmpB);
    }

    public static <T> boolean isSubset(Set<? extends T> setA, Set<? extends T> setB) {
        return setB.containsAll(setA);
    }

    public static <T> boolean isSuperset(Set<? extends T> setA, Set<? extends T> setB) {
        return setA.containsAll(setB);
    }

    public static <T> Set<T> set(T... list) {
        return new HashSet<>(Arrays.asList(list));
    }

    public static <T> Deque<T> queue(T... list) {
        return new ArrayDeque<>(Arrays.asList(list));
    }

    public static <T> List<T> list(T... list) {
        return new ArrayList<>(Arrays.asList(list));
    }

}
