/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.soda.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author antoine
 */
public class Stats {

    public static Double average(Collection<? extends Number> collection) {
        return (Double) sum(collection) / collection.size();
    }

    public static <T extends Number> T sum(Collection<T> collection) {
        T sum = (T) (Number) (Integer) 0;
        for (T item : collection) {
            sum = (T) (Number) (Double) (sum.doubleValue() + item.doubleValue());
        }
        return sum;
    }

}
