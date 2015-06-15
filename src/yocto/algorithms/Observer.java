/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package yocto.algorithms;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.util.ResourceUtils;
import java.util.LinkedList;
import java.util.Queue;
import yocto.rdf.NameSpace;
import yocto.rdf.Predicate;
import yocto.utils.Comparator;
import yocto.utils.Grounder;

/**
 *
 * @author antoine
 */
public class Observer {

    private Model model;

    private Queue<Observation> observations;

    public Observer(Model model) {
        this.model = model;

        observations = new LinkedList<Observation>() {
            {
//                add(new Observation(model.getResource(NameSpace.E + "bob"),
//                        Grounder.instantiateAction(model, NameSpace.A + "go",
//                                NameSpace.E + "bob",
//                                NameSpace.E + "living_room",
//                                NameSpace.E + "bob",
//                                NameSpace.E + "kitchen")));
//                add(new Observation(model.getResource(NameSpace.E + "bob"),
//                        Grounder.instantiateAction(model, NameSpace.A + "grab",
//                                NameSpace.E + "bob",
//                                null,
//                                NameSpace.E + "bob",
//                                NameSpace.E + "dry_pasta")));
            }
        };
    }

    public class Observation {

        public Resource effecter;
        public Resource observation;

        public Observation(Resource effecter, Resource observation) {
            this.effecter = effecter;
            this.observation = observation;
        }

    }

    public Observation next() {
        return observations.remove();
    }

}
