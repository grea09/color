/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package yocto.utils;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.ResourceUtils;
import java.util.HashSet;
import java.util.Set;
import yocto.plannification.Action;
import yocto.plannification.Entity;
import yocto.plannification.Statement;

/**
 *
 * @author antoine
 */
public class Grounder {

    public Set<Entity> favorites;
    private Model model;

    public Grounder(Model model, Set<Entity> favorites) {
        this.favorites = favorites;
        this.model = model;
    }

    public Grounder(Model model) {
        this(model, new HashSet<>());
    }

    public static void ground(Statement statement, String object, String subject) {
        ground(statement.getResource(), object);
        ground(statement.getSubject(), subject);
    }
    
    public static void ground(Resource entity, String uri) {
        if (uri != null) {
            ResourceUtils.renameResource(entity, uri);
        }
    }

    public void groundAction(Action action) {
        for (Statement statement : action.getPreconditions()) {
            ground(statement);
        }

        for (Statement statement : action.getEffects()) {
            ground(statement);
        }
    }

    public void ground(Statement statement) {
        for (Entity favorite : favorites) {
            ground(model.listStatements(favorite, statement.getPredicate(), (String) null), statement);
            ground(model.listStatements(null, statement.getPredicate(), favorite), statement);
        }
        ground(model.listStatements(null, statement.getPredicate(), (String) null), statement);
    }

    private void ground(StmtIterator iterator, Statement statement) {
        while (iterator.hasNext()) {
            Statement next = iterator.next().createReifiedStatement().as(Statement.class);
            if (next.compatible(statement).satisfies()) {
                ground(statement, next.getResource().getURI(), next.getSubject().getURI());
                return;
            }
        }
    }

}
