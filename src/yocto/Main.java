/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package yocto;

import com.hp.hpl.jena.enhanced.BuiltinPersonalities;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ModelMaker;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import yocto.algorithms.Correlation;
import yocto.algorithms.MinPlan;
import yocto.algorithms.PspMinus;
import yocto.plannification.Action;
import yocto.plannification.ActionImpl;
import yocto.plannification.Entity;
import yocto.plannification.EntityImpl;
import yocto.plannification.Goal;
import yocto.plannification.GoalImpl;
import yocto.plannification.Statement;
import yocto.plannification.StatementImpl;
import yocto.utils.Log;
import yocto.utils.ModelManager;

/**
 *
 * @author antoine
 */
public class Main {

    private static final Model model;
    

    static {
        BuiltinPersonalities.model.add(Goal.class, GoalImpl.goalImplementation);
        BuiltinPersonalities.model.add(Action.class, ActionImpl.actionImplementation);
        BuiltinPersonalities.model.add(Statement.class, StatementImpl.statementImplementation);
        BuiltinPersonalities.model.add(Entity.class, EntityImpl.entityImplementation);
        
        model = ModelManager.read("data/test.rdf");
    }

    /**
     * @param args the command line arguments
     * @throws java.io.FileNotFoundException
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        MinPlan minPlan = new MinPlan(model);
        Correlation correlation = new Correlation(model, minPlan);
        //ONLINE
        PspMinus pspMinus = new PspMinus(model, minPlan);
    }

}
