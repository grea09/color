/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.genn.color.planning.domain;

import java.util.Objects;

/**
 *
 * @author antoine
 */
public class Action<F extends Fluent> {

    public static enum Flag {
        DUMMY('?'),
        NORMAL('A'),
        INITIAL('I') {
            @Override
            public boolean special() {
                return true;
            }
        },
        GOAL('G') {
            @Override
            public boolean special() {
                return true;
            }
        };

        private final Character symbol;

        private Flag(Character symbol) {
            this.symbol = symbol;
        }

        @Override
        public String toString() {
            return symbol.toString();
        }

        public boolean special() {
            return false;
        }
    }

    public final State<F> pre;
    public final State<F> eff;
    public final Flag flag;

    public Action(State<F> state, Flag flag) {
        switch (flag) {
            case INITIAL:
                this.eff = new State<>(state, true);
                this.pre = new State<>();
                break;
            default:
                this.pre = new State<>();
                this.eff = new State(state);
        }
        this.flag = flag;
    }

    public Action(State<F> pre, State<F> eff, Flag flag) {
        switch (flag) {
            case INITIAL:
                this.eff = new State<>(eff, true);
                this.pre = new State<>();
                break;
            default:
                this.pre = new State(pre);
                this.eff = new State(eff);
        }
        this.flag = flag;
    }

    public Action(Action other) {
        this(other.pre, other.eff, other.flag);
    }

    @Override
    public String toString() {
        return flag // remove flag for parsing
                + ("<"
                + (pre.isEmpty() ? "" : "pre " + pre)
                + (!pre.isEmpty() && !eff.isEmpty() ? ", ":"")
                + (eff.isEmpty() ? "" : "eff " + eff) + ">");
    }
    
    public boolean special()
    {
        return flag.special();
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 41 * hash + Objects.hashCode(this.pre);
        hash = 41 * hash + Objects.hashCode(this.eff);
        hash = 41 * hash + Objects.hashCode(this.flag);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Action<?> other = (Action<?>) obj;
        if (!Objects.equals(this.pre, other.pre)) {
            return false;
        }
        if (!Objects.equals(this.eff, other.eff)) {
            return false;
        }
        if (this.flag != other.flag) {
            return false;
        }
        return true;
    }
    
    

}
