/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package yocto.utils;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ReifiedStatement;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.ResourceUtils;
import java.util.HashSet;
import java.util.Set;
import yocto.rdf.Predicate;

/**
 *
 * @author antoine
 */
public class Grounder {

    public Set<Resource> favorites;
    private Model model;

    public Grounder(Model model, Set<Resource> favorites) {
        this.favorites = favorites;
        this.model = model;
    }

    public Grounder(Model model) {
        this(model, new HashSet<>());
    }

    public static void ground(Statement statement, Resource object, Resource subject) {
        ground(statement, object.getURI(), subject.getURI());
    }

    public static void ground(Statement statement, String object, String subject) {
        if (object != null) {
            ResourceUtils.renameResource(statement.getResource(), object);
        }
        if (subject != null) {
            ResourceUtils.renameResource(statement.getSubject(), subject);
        }
    }

    public void groundAction(Resource action) {
        StmtIterator iterator = action.getModel().listStatements(action, Predicate.PRECONDITION.in(action.getModel()), (String) null);
        while (iterator.hasNext()) {
            ground(iterator.next().getSubject().as(ReifiedStatement.class).getStatement());
        }

        iterator = action.getModel().listStatements(action, Predicate.EFFECT.in(action.getModel()), (String) null);
        while (iterator.hasNext()) {
            ground(iterator.next().getSubject().as(ReifiedStatement.class).getStatement());
        }
    }

    public void ground(Statement statement) {
        for (Resource favorite : favorites) {
            StmtIterator iterator = model.listStatements(favorite, statement.getPredicate(), (String) null);
            while (iterator.hasNext()) {
                Statement next = iterator.next();
                if (Comparator.propertiesSatisfying(next, statement)) {
                    ground(statement, next.getResource(), next.getSubject());
                    return;
                }
            }
            iterator = model.listStatements(null, statement.getPredicate(), favorite);
            while (iterator.hasNext()) {
                Statement next = iterator.next();
                if (Comparator.propertiesSatisfying(next, statement)) {
                    ground(statement, next.getResource(), next.getSubject());
                    return;
                }
            }
        }
        StmtIterator iterator = model.listStatements(null, statement.getPredicate(), (String) null);
        while (iterator.hasNext()) {
            Statement next = iterator.next();
            if (Comparator.propertiesSatisfying(next, statement)) {
                ground(statement, next.getResource(), next.getSubject());
                return;
            }
        }
    }

}
