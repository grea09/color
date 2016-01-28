/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package yocto.plannification;

import com.hp.hpl.jena.enhanced.EnhGraph;
import com.hp.hpl.jena.enhanced.EnhNode;
import com.hp.hpl.jena.enhanced.Implementation;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.rdf.model.Alt;
import com.hp.hpl.jena.rdf.model.Bag;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.RSIterator;
import com.hp.hpl.jena.rdf.model.ReifiedStatement;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceF;
import com.hp.hpl.jena.rdf.model.Seq;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.rdf.model.impl.ReifiedStatementImpl;
import yocto.rdf.NameSpace;
import me.grea.antoine.utils.Log;

public class StatementImpl extends ReifiedStatementImpl implements Statement {

    static final public Implementation statementImplementation = new Implementation() {

        @Override
        public EnhNode wrap(Node n, EnhGraph eg) {
            ReifiedStatement reifiedStatement = (ReifiedStatement) reifiedStatementFactory.wrap(n, eg);
            return new StatementImpl(eg, reifiedStatement.asNode(), reifiedStatement.getStatement());
        }

        @Override
        public boolean canWrap(Node n, EnhGraph eg) {
            return reifiedStatementFactory.canWrap(n, eg);
        }
    };

    protected StatementImpl(EnhGraph m, Node n, com.hp.hpl.jena.rdf.model.Statement s) {
        super(m, n, s);
    }

    @Override
    public Resource getSubject() {
        return statement.getSubject();
    }

    @Override
    public Property getPredicate() {
        return statement.getPredicate();
    }

    @Override
    public RDFNode getObject() {
        return statement.getObject();
    }

    @Override
    public com.hp.hpl.jena.rdf.model.Statement getStatementProperty(Property p) {
        return statement.getStatementProperty(p);
    }

    @Override
    public Resource getResource() {
        return statement.getResource();
    }

    @Override
    public Literal getLiteral() {
        return statement.getLiteral();
    }

    @Override
    public boolean getBoolean() {
        return statement.getBoolean();
    }

    @Override
    public byte getByte() {
        return statement.getByte();
    }

    @Override
    public short getShort() {
        return statement.getShort();
    }

    @Override
    public int getInt() {
        return statement.getInt();
    }

    @Override
    public long getLong() {
        return statement.getLong();
    }

    @Override
    public char getChar() {
        return statement.getChar();
    }

    @Override
    public float getFloat() {
        return statement.getFloat();
    }

    @Override
    public double getDouble() {
        return statement.getDouble();
    }

    @Override
    public String getString() {
        return statement.getString();
    }

    @Override
    public Resource getResource(ResourceF f) {
        return statement.getResource(f);
    }

    @Override
    public Bag getBag() {
        return statement.getBag();
    }

    @Override
    public Alt getAlt() {
        return statement.getAlt();
    }

    @Override
    public Seq getSeq() {
        return statement.getSeq();
    }

    @Override
    public String getLanguage() {
        return statement.getLanguage();
    }

    @Override
    public boolean hasWellFormedXML() {
        return statement.hasWellFormedXML();
    }

    @Override
    public com.hp.hpl.jena.rdf.model.Statement changeLiteralObject(boolean o) {
        return statement.changeLiteralObject(o);
    }

    @Override
    public com.hp.hpl.jena.rdf.model.Statement changeLiteralObject(long o) {
        return statement.changeLiteralObject(o);
    }

    @Override
    public com.hp.hpl.jena.rdf.model.Statement changeLiteralObject(int o) {
        return statement.changeLiteralObject(o);
    }

    @Override
    public com.hp.hpl.jena.rdf.model.Statement changeLiteralObject(char o) {
        return statement.changeLiteralObject(o);
    }

    @Override
    public com.hp.hpl.jena.rdf.model.Statement changeLiteralObject(float o) {
        return statement.changeLiteralObject(o);
    }

    @Override
    public com.hp.hpl.jena.rdf.model.Statement changeLiteralObject(double o) {
        return statement.changeLiteralObject(o);
    }

    @Override
    public com.hp.hpl.jena.rdf.model.Statement changeObject(String o) {
        return statement.changeObject(o);
    }

    @Override
    public com.hp.hpl.jena.rdf.model.Statement changeObject(String o, boolean wellFormed) {
        return statement.changeObject(o, wellFormed);
    }

    @Override
    public com.hp.hpl.jena.rdf.model.Statement changeObject(String o, String l) {
        return statement.changeObject(o, l);
    }

    @Override
    public com.hp.hpl.jena.rdf.model.Statement changeObject(String o, String l, boolean wellFormed) {
        return statement.changeObject(o, l, wellFormed);
    }

    @Override
    public com.hp.hpl.jena.rdf.model.Statement changeObject(RDFNode o) {
        return statement.changeObject(o);
    }

    @Override
    public com.hp.hpl.jena.rdf.model.Statement remove() {
        return statement.remove();
    }

    @Override
    public boolean isReified() {
        return statement.isReified();
    }

    @Override
    public ReifiedStatement createReifiedStatement() {
        return this;
    }

    @Override
    public ReifiedStatement createReifiedStatement(String uri) {
        return statement.createReifiedStatement(uri);
    }

    @Override
    public RSIterator listReifiedStatements() {
        return statement.listReifiedStatements();
    }

    @Override
    public void removeReification() {
        statement.removeReification();
    }

    @Override
    public Triple asTriple() {
        return statement.asTriple();
    }

    @Override
    public Compatibility compatible(Statement o) {
        if (!getPredicate().equals(o.getPredicate())) {
            return Compatibility.INCOMPATIBLE;
        }

        return this.getResource().as(Entity.class).compatible(
                o.getResource().as(Entity.class)).and(
                this.getSubject().as(Entity.class).compatible(
                o.getSubject().as(Entity.class))
        );
    }

    @Override
    public boolean isSatisfied(Model context) {
//        Log.d(this + " isSatisfied ?");
        StmtIterator iterator = context.listStatements(null,
                getPredicate(),
                (String) null
        );

        while (iterator.hasNext()) {
            Statement contextStatement = iterator.next().createReifiedStatement().as(Statement.class);
//            Log.d(contextStatement);
            if (compatible(contextStatement).satisfies()) {
//                Log.d("SATISFIES");
                iterator.close();
                return true;
            }
        }
        iterator.close();
        return false;
    }

    @Override
    public String toString() {
        return "" + statement.getSubject().as(Entity.class)+ 
                " "+ NameSpace.prefix(statement.getPredicate().getURI()) + 
                " " + (statement.getObject().canAs(Entity.class) ? statement.getObject().as(Entity.class) : statement.getObject().toString());
    }
    
    

}
