/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package yocto.algorithms;

import com.hp.hpl.jena.enhanced.BuiltinPersonalities;
import com.hp.hpl.jena.rdf.model.Model;
import java.util.List;
import java.util.Set;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import yocto.plannification.Action;
import yocto.plannification.ActionImpl;
import yocto.plannification.Entity;
import yocto.plannification.EntityImpl;
import yocto.plannification.Goal;
import yocto.plannification.GoalImpl;
import yocto.plannification.Statement;
import yocto.plannification.StatementImpl;
import yocto.utils.ModelManager;
import yocto.utils.Query;

/**
 *
 * @author antoine
 */
public class CorrelationTest {
    
    public CorrelationTest() {
    }
    
    private static Model model;
    
    @Test(timeout=300)
    public void testInit() {
        MinPlan minPlan = new MinPlan(model);
        Correlation correlation = new Correlation(model, minPlan);
        assertNotNull(correlation.correlation);
        int n = minPlan.goals.size();
        assertEquals(correlation.correlation.size(), n*(n-1)/2);
        for(List<Correlation.Count> counts : correlation.correlation.values())
        {
            assertEquals(counts.size(), 2);
            assertTrue(counts.get(0).correlation(counts.get(1)) < 1.0);
        }
    }
    
    @BeforeClass
    public static void setUpClass() {
        BuiltinPersonalities.model.add(Goal.class, GoalImpl.goalImplementation);
        BuiltinPersonalities.model.add(Action.class, ActionImpl.actionImplementation);
        BuiltinPersonalities.model.add(Statement.class, StatementImpl.statementImplementation);
        BuiltinPersonalities.model.add(Entity.class, EntityImpl.entityImplementation);
        
        model = ModelManager.read("data/test.rdf");
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of toString method, of class MinPlan.
     */
    @Test(timeout=300)
    public void testToString() {
        System.out.println("toString");
        MinPlan minPlan = new MinPlan(model);
        Correlation correlation = new Correlation(model, minPlan);
        String expResult = "Correlation g:clean ~ g:watch_tv ~ 0.1111111111111111\n" +
"Correlation Correlation g:clean ~ g:watch_tv ~ 0.1111111111111111\n" +
"g:cook ~ g:watch_tv ~ 0.1111111111111111\n" +
"Correlation Correlation g:clean ~ g:watch_tv ~ 0.1111111111111111\n" +
"Correlation Correlation g:clean ~ g:watch_tv ~ 0.1111111111111111\n" +
"g:cook ~ g:watch_tv ~ 0.1111111111111111\n" +
"g:clean ~ g:cook ~ 0.6666666666666666\n" +
"";
        String result = correlation.toString();
        assertEquals(expResult, result);
    }
    
}
