/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.soda.test;

import me.grea.antoine.utils.Dice;
import me.grea.antoine.soda.test.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import me.grea.antoine.soda.type.Action;
import me.grea.antoine.soda.type.Problem;
import me.grea.antoine.soda.type.flaw.Flaw;
import static me.grea.antoine.utils.Collections.*;
import static java.lang.Math.*;
import me.grea.antoine.utils.Log;

/**
 *
 * @author antoine
 */
public class ValidActionGenerator {

    public static Set<Action> generate(int enthropy, int number) {
        Set<Action> actions = new HashSet<>(number);
        while (actions.size() < number) {
            actions.add(generate(enthropy));
        }
        return actions;
    }

    public static Action generate(int enthropy) {
        Action action = new Action(Dice.rollNonZero(-enthropy, enthropy, Dice.roll((int) round(log10(enthropy) + 1))),
                Dice.rollNonZero(-enthropy, enthropy, Dice.roll((int) round(log10(enthropy) + 1))));
        clean(action);
        return action;
    }

    public static Action generateWith(Set<Integer> provided, int effect) {
        Action action = new Action(Dice.pick(provided, Dice.roll(provided.size())), new HashSet<>());
        action.effects.add(effect);
        clean(action);
        return action;
    }

    public static void clean(Action action) {
        action.preconditions.removeAll(action.effects);
        Set<Integer> dirty = new HashSet<>(action.effects);
        Set<Integer> cleaned = new HashSet<>();
        for (int effect : dirty) {
            if (!cleaned.contains(effect)) {
                action.effects.remove(-effect);
                cleaned.add(-effect);
            }
        }
        dirty = new HashSet<>(action.preconditions);
        cleaned = new HashSet<>();
        for (int precondition : dirty) {
            if (!cleaned.contains(precondition)) {
                action.preconditions.remove(-precondition);
                cleaned.add(-precondition);
            }
        }
    }
}
