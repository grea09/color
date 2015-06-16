/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package yocto.plannification;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ReifiedStatement;
import yocto.utils.Compatible;

/**
 *
 * @author antoine
 */
public interface Statement extends ReifiedStatement, com.hp.hpl.jena.rdf.model.Statement, Compatible<Statement> {

    public boolean isSatisfied(Model context);
    
}
