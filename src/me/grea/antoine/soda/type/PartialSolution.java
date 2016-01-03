/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.soda.type;

import java.util.ArrayDeque;
import java.util.Deque;
import me.grea.antoine.soda.type.flaw.Flaw;

/**
 *
 * @author antoine
 */
public class PartialSolution {

    public Plan plan;
    public Flaw cause;
    public Deque<Flaw> remaining;

    public PartialSolution(Plan plan, Flaw cause, Deque<Flaw> remaining) {
        this.plan = new Plan(plan);
        this.cause = cause;
        this.remaining = new ArrayDeque<>(remaining);
    }

}
