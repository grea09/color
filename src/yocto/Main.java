/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package yocto;

import com.hp.hpl.jena.enhanced.BuiltinPersonalities;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import yocto.algorithms.Correlation;
import yocto.algorithms.MinPlan;
import yocto.plannification.Action;
import yocto.plannification.ActionImpl;
import yocto.plannification.Goal;
import yocto.plannification.GoalImpl;
import yocto.utils.Log;

/**
 *
 * @author antoine
 */
public class Main {

    private static final Model model;

    static {
        BuiltinPersonalities.model.add(Action.class, ActionImpl.actionImplementation);
        BuiltinPersonalities.model.add(Goal.class, GoalImpl.goalImplementation);
        model = ModelFactory.createMemModelMaker().createDefaultModel();
        InputStream in = null;
        try {
            // Open the bloggers RDF graph from the filesystem
            in = new FileInputStream(new File("data/test.rdf"));
            model.read(in, null); // null base URI, since model URIs are absolute
        } catch (FileNotFoundException ex) {
            Log.e(ex);
        } finally {
            try {
                in.close();
            } catch (IOException ex) {
                Log.e(ex);
            }
        }

    }

    /**
     * @param args the command line arguments
     * @throws java.io.FileNotFoundException
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        MinPlan minPlan = new MinPlan(model);
        Correlation correlation = new Correlation(model, minPlan);
        //ONLINE
//        PspMinus pspMinus = new PspMinus(model, minPlan);
    }

}
