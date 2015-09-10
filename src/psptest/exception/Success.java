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
public class Success extends Exception {
    public Object result;

    public Success(Object result) {
        this.result = result;
    }
    
}
