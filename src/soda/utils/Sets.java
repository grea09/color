/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package soda.utils;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author http://www.java2s.com/Code/Java/Collections-Data-Structure/Setoperationsunionintersectiondifferencesymmetricdifferenceissubsetissuperset.htm
 */
public class Sets {
    public static <T> Set<T> union(Set<T> setA, Set<T> setB) {
    Set<T> tmp = new HashSet<>(setA);
    tmp.addAll(setB);
    return tmp;
  }

  public static <T> Set<T> intersection(Set<T> setA, Set<T> setB) {
    Set<T> tmp = new HashSet<>();
    setA.stream().filter((x) -> (setB.contains(x))).forEach((x) -> {
        tmp.add(x);
    });
    return tmp;
  }

  public static <T> Set<T> difference(Set<T> setA, Set<T> setB) {
    Set<T> tmp = new HashSet<>(setA);
    tmp.removeAll(setB);
    return tmp;
  }

  public static <T> Set<T> symDifference(Set<T> setA, Set<T> setB) {
    Set<T> tmpA;
    Set<T> tmpB;

    tmpA = union(setA, setB);
    tmpB = intersection(setA, setB);
    return difference(tmpA, tmpB);
  }

  public static <T> boolean isSubset(Set<T> setA, Set<T> setB) {
    return setB.containsAll(setA);
  }

  public static <T> boolean isSuperset(Set<T> setA, Set<T> setB) {
    return setA.containsAll(setB);
  }
    
}
