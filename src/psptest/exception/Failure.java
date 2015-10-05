/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package psptest.exception;

/**
 *
 * @author antoine
 */
public class Failure extends Exception {
    public Object cause;

    public Failure(Object cause) {
        this.cause = cause;
    }
    
}
