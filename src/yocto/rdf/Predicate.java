/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package yocto.rdf;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

/**
 *
 * @author antoine
 */
public enum Predicate {
    TYPE(NameSpace.RDF + "type"),
    OBJECT(NameSpace.RDF + "object"),
    PREDICATE(NameSpace.RDF + "predicate"),
    SUBJECT(NameSpace.RDF + "subject"),
    PRECONDITION(NameSpace.BASE + "pre"),
    EFFECT(NameSpace.BASE + "eff"),
    PARAM(NameSpace.BASE + "param");
    
    private final String uri;
    
    private Predicate(String uri)
    {
        this.uri = uri;
    }
    
    @Override
    public String toString() {
        return uri;
    }
    
    public Property in(Model model)
    {
        return model.getProperty(uri);
    }
    
    public Property in(Resource resource)
    {
        return in(resource.getModel());
    }
    
    public Property property()
    {
        return ResourceFactory.createProperty(uri);
    }
    
    public Node node()
    {
        return property().asNode();
    }
}
