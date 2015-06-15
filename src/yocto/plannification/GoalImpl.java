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
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.ontology.impl.IndividualImpl;
import com.hp.hpl.jena.rdf.model.DoesNotReifyException;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ReifiedStatement;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.rdf.model.impl.ReifiedStatementImpl;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.vocabulary.RDF;
import java.util.HashSet;
import java.util.Set;
import yocto.rdf.NameSpace;
import yocto.rdf.Predicate;

public class GoalImpl extends IndividualImpl implements Goal {

    private final static Node type = NameSpace.BASE.node("Goal");

    private final Set<Statement> preconditions;

    protected GoalImpl(EnhGraph m, Node n, Set<Statement> preconditions) {
        super(n, m);
        this.preconditions = preconditions;
    }

    @Override
    public Set<Statement> getPreconditions() {
        return preconditions;
    }

    static final public Implementation goalImplementation = new Implementation() {

        @Override
        public EnhNode wrap(Node node, EnhGraph eg) {
            Graph graph = eg.asGraph();
            // Must have rdf:type rdf:Statement
            if (!graph.contains(node, RDF.type.asNode(), type)) {
                throw new DoesNotReifyException(node);
            }

            Set<Statement> preconditions = get(node, Predicate.PRECONDITION.node(), eg);

            if (preconditions.isEmpty()) {
                throw new DoesNotReifyException(node);
            }

            return new GoalImpl(eg, node, preconditions);

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

    static protected Set<Statement> get(Node action, Node predicate, EnhGraph eg) {
        return new HashSet<Statement>() {
            {
                ExtendedIterator<Triple> iterator = eg.asGraph().find(action, predicate, Node.ANY);
                while (iterator.hasNext()) {
                    Node pre = iterator.next().getObject();
                    add(ReifiedStatementImpl.reifiedStatementFactory.wrap(pre, eg).as(ReifiedStatement.class).getStatement());
                }
                iterator.close();
            }
        };
    }

}
