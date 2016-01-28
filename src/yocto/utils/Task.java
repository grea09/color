/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package yocto.utils;

/**
 *
 * @author antoine
 */
public class Task<R, P> extends Thread {
    private final Parallelized<R, P> code;
    private final P parameters;
    private Object result;
    
    public Task(Parallelized<R,P> code, P parameters)
    {
        this.code = code;
        this.parameters = parameters;
    }
    
    public R result() throws InterruptedException
    {
        result.wait();
        return (R) result;
    }

    @Override
    public void run() {
        R returned = code.run(parameters);
        result.notifyAll();
        result = returned;
    }
    
    
    
}
