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
import com.hp.hpl.jena.rdf.model.DoesNotReifyException;
import com.hp.hpl.jena.vocabulary.RDF;
import java.util.Set;
import yocto.rdf.NameSpace;
import yocto.rdf.Predicate;

public class ActionImpl extends GoalImpl implements Action {

    private final static Node type = NameSpace.BASE.node("Action");

    private final Set<Statement> effects;

    protected ActionImpl(Node n, EnhGraph m, Set<Statement> preconditions, Set<Statement> effects) {
        super(n, m, preconditions);
        this.effects = effects;
    }

    @Override
    public Set<Statement> getEffects() {
        return effects;
    }

    @Override
    public int compareTo(Action o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    static final public Implementation actionImplementation = new Implementation() {

        @Override
        public EnhNode wrap(Node node, EnhGraph eg) {
            Graph graph = eg.asGraph();
            // Must have rdf:type rdf:Statement
            if (!graph.contains(node, RDF.type.asNode(), type)) {
                throw new DoesNotReifyException(node);
            }

            Set<Statement> preconditions = get(node, Predicate.PRECONDITION.node(), eg);

            Set<Statement> effects = get(node, Predicate.EFFECT.node(), eg);

            if (preconditions.isEmpty() || effects.isEmpty()) {
                throw new DoesNotReifyException(node);
            }

            return new ActionImpl(node, eg, preconditions, effects);

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

}
