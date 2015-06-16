/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package yocto.plannification;

import com.hp.hpl.jena.enhanced.EnhGraph;
import com.hp.hpl.jena.enhanced.EnhNode;
import com.hp.hpl.jena.enhanced.Implementation;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.ontology.impl.IndividualImpl;
import com.hp.hpl.jena.rdf.model.DoesNotReifyException;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import yocto.rdf.NameSpace;
import yocto.rdf.Predicate;
import yocto.utils.Compatible.Compatibility;

public class EntityImpl extends IndividualImpl implements Entity {

    private final static Node type = NameSpace.BASE.node("Entity");

    static final public Implementation entityImplementation = new Implementation() {

        @Override
        public EnhNode wrap(Node node, EnhGraph eg) {
            Graph graph = eg.asGraph();
            // Must have rdf:type rdf:Statement
//            if (!graph.contains(node, RDF.type.asNode(), type)) {
//                throw new DoesNotReifyException(node);
//            }

            return new EntityImpl(node, eg);

        }

        @Override
        public boolean canWrap(Node node, EnhGraph eg) {
            try {
                wrap(node, eg);
            } catch (DoesNotReifyException ex) {
                return false;
            }
            return true;
        }

    };

    public EntityImpl(Node n, EnhGraph g) {
        super(n, g);
    }

    @Override
    public Compatibility compatible(Entity o) {
        Model e1Model = getModel().query(new SimpleSelector(this, (Property) null, (String) null));
        Model e2Model = o.getModel().query(new SimpleSelector(o, (Property) null, (String) null));
        e1Model.removeAll(null, Predicate.PARAM.in(e1Model), null);
        e2Model.removeAll(null, Predicate.PARAM.in(e2Model), null);
        Model intersection = e1Model.intersection(e2Model);

        return e1Model.isIsomorphicWith(e2Model) ? Compatibility.COMPATIBLE
                : (intersection.isIsomorphicWith(e2Model) ? Compatibility.SATISFIED
                        : (intersection.isIsomorphicWith(e1Model) ? Compatibility.SATISFIES : Compatibility.INCOMPATIBLE));
    }

    @Override
    public String toString() {
        return (isAnon() ? "param:" + NameSpace.prefix(getPropertyResourceValue(Predicate.PARAM.in(getModel())).getURI()) : NameSpace.prefix(getURI()));
    }
    
    

}
