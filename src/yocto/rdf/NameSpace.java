/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package yocto.rdf;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author antoine
 */
public enum NameSpace {

    BASE("http://yocto.fr") {
                @Override
                public String prefix() {
                    return "";
                }
            },
    RDF("http://www.w3.org/1999/02/22-rdf-syntax-ns"),
    A(BASE.uri + "/action"),
    C(BASE.uri + "/class"),
    E(BASE.uri + "/entity"),
    P(BASE.uri + "/property"),
    G(BASE.uri + "/goal");

    private final String uri;

    private NameSpace(String uri) {
        this.uri = uri;
    }

    @Override
    public String toString() {
        return uri + '#';
    }

    public String prefix() {
        return name().toLowerCase();
    }

    public static final Map<String, String> prefixes
            = new HashMap<String, String>() {
                {
                    for (NameSpace ns : NameSpace.values()) {
                        put(ns.prefix(), ns.toString());
                    }
                }
            };

    public Resource resource(String local) {
        return ResourceFactory.createResource(this + local);
    }

    public Node node(String local) {
        return resource(local).asNode();
    }
    
    public static String strip(String uri)
    {
        return uri.replaceFirst("http:[^#]*#", "");
    }
    
    public static String prefix(String uri)
    {
        for(Map.Entry<String, String> entry : prefixes.entrySet())
        {
            if(uri.startsWith(entry.getValue()))
            {
                return entry.getKey() + ":" + strip(uri);
            }
        }
        return uri;
    }
}
