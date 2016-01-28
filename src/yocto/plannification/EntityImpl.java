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
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;
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
        if (hasURI(NameSpace.E + "nothing") || o.hasURI(NameSpace.E + "nothing")) {
            return equals(o) ? Compatibility.COMPATIBLE : Compatibility.INCOMPATIBLE;
        }
        Compatibility compatibility = Compatibility.COMPATIBLE;
        StmtIterator iterator = listProperties();
        while (iterator.hasNext()) {
            com.hp.hpl.jena.rdf.model.Statement statement = iterator.next();
            if (!statement.getPredicate().hasURI(Predicate.PARAM.toString())
                    && !o.hasProperty(statement.getPredicate(), statement.getResource())) {
                //if o doesn't have a property this has then this.compatible(o) = SATISFIES (this > o)
                compatibility = compatibility.and(Compatibility.SATISFIED);
                break;
            }
        }
        iterator = o.listProperties();
        while (iterator.hasNext()) {
            com.hp.hpl.jena.rdf.model.Statement statement = iterator.next();
            if (!statement.getPredicate().hasURI(Predicate.PARAM.toString())
                    && !hasProperty(statement.getPredicate(), statement.getResource())) {
                //if this doesn't have a property o has then this.compatible(o) = SATISFIED (o > this)
                compatibility = compatibility.and(Compatibility.SATISFIES);
                break;
            }
        }
        // if both then incompatible !
        return compatibility;
    }

    @Override
    public String toString() {
        return (isAnon()
                ? (hasProperty(Predicate.PARAM.in(getModel()))
                        ? "param:" + NameSpace.prefix(getPropertyResourceValue(Predicate.PARAM.in(getModel())).getURI())
                        : "?")
                : NameSpace.prefix(getURI()));
    }

}
