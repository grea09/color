/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.lollipop.exception;

import me.grea.antoine.lollipop.type.flaw.Flaw;

/**
 *
 * @author antoine
 */
public class Failure extends Exception {

    public Flaw cause;

    public Failure(Flaw cause) {
        this.cause = cause;
    }

    @Override
    public String getMessage() {
        return "Failure caused by " + cause;
    }

}
