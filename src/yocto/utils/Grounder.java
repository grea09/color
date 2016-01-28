/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package yocto.utils;

import me.grea.antoine.utils.Log;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.ResourceUtils;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import yocto.plannification.Action;
import yocto.plannification.Entity;
import yocto.plannification.Statement;
import yocto.rdf.NameSpace;

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

    public static Resource ground(Resource entity, String uri) {
        if (uri != null && !uri.equals(entity.getURI())) {
//            ResourceUtils.renameResource(entity, uri);
            Log.v("↓" + entity.as(Entity.class) + " => " + NameSpace.prefix(uri));
//            Log.v("BEFORE");
//            entity.getModel().write(Log.out, "TTL");
            Resource newEntity = entity.getModel().createResource(uri);
            StmtIterator iterator = entity.listProperties();
            while (iterator.hasNext()) {
                com.hp.hpl.jena.rdf.model.Statement statement = iterator.next();
                newEntity.addProperty(statement.getPredicate(), statement.getObject());
            }
            iterator = entity.getModel().listStatements(null, null, entity);
            while (iterator.hasNext()) {
                com.hp.hpl.jena.rdf.model.Statement statement = iterator.next();
                entity.getModel().add(statement.getSubject(), statement.getPredicate(), newEntity);
            }
            entity.removeProperties();
            entity.getModel().removeAll(null, null, entity);
//            Log.v("AFTER");
//            newEntity.getModel().write(Log.out, "TTL");
            return newEntity;
        }
        return entity;
    }

    public void groundAction(Action action) {
        Set<Statement> statements = new HashSet<Statement>() {
            {
                addAll(action.getPreconditions());
                addAll(action.getEffects());
            }
        };
        Map<Entity, Entity> replacement = new HashMap<>();
        for (Statement statement : statements) {
            Statement match = match(statement);
            if (match != null) {
                replacement.put(statement.getSubject().as(Entity.class), match.getSubject().as(Entity.class));
                replacement.put(statement.getResource().as(Entity.class), match.getResource().as(Entity.class));
            }
        }
        for (Map.Entry<Entity, Entity> entry : replacement.entrySet()) {
            replacement.put(entry.getKey(), ground(entry.getKey(), entry.getValue().getURI()).as(Entity.class));
        }

        Set<Statement> tmp = new HashSet<>(action.getPreconditions());
        action.getPreconditions().clear();
        for (Statement statement : tmp) {
            action.getPreconditions().add(
                    statement.getModel().createStatement(
                            replacement.get(statement.getSubject().as(Entity.class)),
                            statement.getPredicate(),
                            replacement.get(statement.getResource().as(Entity.class))
                    ).createReifiedStatement().as(Statement.class));
        }

        tmp = new HashSet<>(action.getEffects());
        action.getEffects().clear();
        for (Statement statement : tmp) {
            Entity subject = replacement.get(statement.getSubject().as(Entity.class));
            Entity object = replacement.get(statement.getResource().as(Entity.class));
            if (subject == null) {
                subject = statement.getSubject().as(Entity.class);
            }
            if (object == null) {
                object = statement.getResource().as(Entity.class);
            }

            action.getEffects().add(
                    statement.getModel().createStatement(
                            subject,
                            statement.getPredicate(),
                            object
                    ).createReifiedStatement().as(Statement.class));
        }
    }

    private Statement match(Statement statement) {
        Statement match = null;
        for (Entity favorite : favorites) {
            match = match(model.listStatements(favorite, statement.getPredicate(), (RDFNode) null), statement);
            if (match != null) {
                return match;
            }
            match = match(model.listStatements(null, statement.getPredicate(), favorite), statement);
            if (match != null) {
                return match;
            }
        }
        match = match(model.listStatements(null, statement.getPredicate(), (RDFNode) null), statement);
        if (match != null) {
            return match;
        }
        return null;
    }

    private Statement match(StmtIterator iterator, Statement statement) {
        while (iterator.hasNext()) {
            Statement next = iterator.next().createReifiedStatement().as(Statement.class);
            if (statement.compatible(next).satisfies()) {
                return next;
            }
        }
        return null;
    }

}
