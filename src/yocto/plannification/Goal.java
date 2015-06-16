/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package yocto.plannification;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import java.util.Set;

/**
 *
 * @author antoine
 */
public interface Goal extends Resource {
    
    public boolean isSatisfied(Model initial);

    public Set<Statement> getPreconditions();

}
