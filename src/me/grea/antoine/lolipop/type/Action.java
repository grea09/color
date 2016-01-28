/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.lolipop.type;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author antoine
 */
public class Action extends Goal {

    public Set<Integer> effects;
    public final boolean fake;
    private final char symbol;

    public Action() {
        this(new HashSet<>(), new HashSet<>());
    }

    public Action(Goal goal) {
        this(goal.preconditions, new HashSet());
    }

    public Action(Action action) {
        this(action.preconditions, action.effects);
    }

    public Action(Collection<Integer> preconditions, Collection<Integer> effects) {
        this(preconditions, effects, false);
    }

    public Action(Collection<Integer> preconditions, Collection<Integer> effects, boolean fake) {
        super(preconditions);
        symbol = fake ? '₳' :(effects == null ? 'G' : (preconditions == null ? 'I' : 'A'));
        if (effects == null) {
            this.effects = new HashSet();
        } else {
            this.effects = new HashSet(effects);
        }
        this.fake = fake;
    }

    @Override
    public String toString() {
        return symbol + (symbol == 'G' || symbol == 'I' ? "" : ("("
                + (preconditions.isEmpty() ? "" : "⊧" + preconditions)
                + (effects.isEmpty() ? "" : "+" + effects) + ")"));
    }
}
