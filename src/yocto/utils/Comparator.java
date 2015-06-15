/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package yocto.utils;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ReifiedStatement;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import yocto.rdf.Predicate;

/**
 *
 * @author antoine
 */
public class Comparator {

    public static boolean satisfied(Statement statement, Model context) {
        StmtIterator iterator = context.listStatements(null,
                statement.getPredicate(),
                (String) null
        );

        while (iterator.hasNext()) {
            Statement contextStatement = iterator.next();
            if (propertiesSatisfying(contextStatement, statement)) {
                return true;
            }
        }

        return false;
    }

    public static boolean propertiesSatisfying(Statement pre, Statement post) {
        if (!pre.getPredicate().equals(post.getPredicate())) {
            return false;
        }

        return entitiesSatisfying(pre.getResource(), post.getResource())
                && entitiesSatisfying(pre.getSubject(), post.getSubject());
    }

    public static boolean entitiesSatisfying(Resource sub, Resource sup) {
        Model subModel = sub.getModel().query(new SimpleSelector(sub, (Property) null, (String) null));
        Model supModel = sup.getModel().query(new SimpleSelector(sup, (Property) null, (String) null));
        subModel.removeAll(null, Predicate.PARAM.in(subModel), null);
        supModel.removeAll(null, Predicate.PARAM.in(supModel), null);

        return subModel.intersection(supModel).isIsomorphicWith(supModel);
    }

    public static boolean propertiesCompatible(Statement p1, Statement p2) {
        if (!p1.getPredicate().equals(p2.getPredicate())) {
            return false;
        }

        return entitiesCompatible(p1.getResource(), p2.getResource())
                && entitiesCompatible(p1.getSubject(), p2.getSubject());
    }

    public static boolean entitiesCompatible(Resource e1, Resource e2) {
        Model e1Model = e1.getModel().query(new SimpleSelector(e1, (Property) null, (String) null));
        Model e2Model = e2.getModel().query(new SimpleSelector(e2, (Property) null, (String) null));
        e1Model.removeAll(null, Predicate.PARAM.in(e1Model), null);
        e2Model.removeAll(null, Predicate.PARAM.in(e2Model), null);

        return e1Model.isIsomorphicWith(e2Model);
    }

}
