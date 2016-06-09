/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.liris.lollipop2.planning.domain;

/**
 *
 * @author antoine
 */
public class IntFluent implements Fluent<IntFluent>{

    public final int value;
    
    public IntFluent(int value) {
        this.value = value;
    }
    
    public IntFluent(IntFluent other) {
        this(other.value);
    }

    @Override
    public boolean unifies(IntFluent lesser) {
        return this.value == lesser.value;
    }

    @Override
    public boolean contradicts(IntFluent counter) {
       return this.value == -counter.value;
    }

    @Override
    public IntFluent instanciate(IntFluent lesser) {
        assert(unifies(lesser));
        return this;
    }

    @Override
    public int hashCode() {
        return value;
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
        final IntFluent other = (IntFluent) obj;
        if (this.value != other.value) {
            return false;
        }
        return true;
    }

    @Override
    public IntFluent negate() {
        return new IntFluent(-value);
    }

    @Override
    public boolean negative() {
        return value<0;
    }

    @Override
    public String toString() {
        return "" + value ;
    }
    
    
    
}
