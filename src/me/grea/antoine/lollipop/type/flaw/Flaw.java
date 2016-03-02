/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.lollipop.type.flaw;

import java.util.Deque;
import java.util.Objects;
import java.util.Set;
import me.grea.antoine.lollipop.type.Action;
import me.grea.antoine.lollipop.type.Problem;

/**
 *
 * @author antoine
 * @param <T> Subtype
 */
public abstract class Flaw<T extends Flaw> {

    public int fluent;
    public Action needer;
    public Problem problem;

    protected Flaw(Problem problem) {
        this.problem = problem;
    }

    public Flaw(int fluent, Action needer, Problem problem) {
        this.fluent = fluent;
        this.needer = needer;
        this.problem = problem;
    }

    public abstract Deque<Resolver> resolvers();

    public abstract Set<T> related(Action troubleMaker);

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
        final Flaw other = (Flaw) obj;
        if (this.fluent != other.fluent) {
            return false;
        }
        if (!Objects.equals(this.needer, other.needer)) {
            return false;
        }
        if (!Objects.equals(this.problem, other.problem)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + this.fluent;
        hash = 67 * hash + Objects.hashCode(this.needer);
        hash = 67 * hash + Objects.hashCode(this.problem);
        return hash;
    }

}
