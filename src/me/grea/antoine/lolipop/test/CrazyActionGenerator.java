/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.lolipop.test;

import me.grea.antoine.utils.Dice;
import java.util.HashSet;
import java.util.Set;
import me.grea.antoine.lolipop.type.Action;

/**
 *
 * @author antoine
 */
public class CrazyActionGenerator {

    public static Set<Action> generate(int enthropy, int number) {
        Set<Action> actions = new HashSet<>(number);
        while (actions.size() < number) {
            actions.add(new Action(Dice.rollNonZero(-enthropy, enthropy, Dice.roll((int) (Math.log10(enthropy) + 1))),
                    Dice.rollNonZero(-enthropy, enthropy, Dice.roll((int) (Math.log10(enthropy) + 1)))));
        }
        return actions;
    }
}
