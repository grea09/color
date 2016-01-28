/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package yocto.algorithms;

import com.hp.hpl.jena.enhanced.BuiltinPersonalities;
import com.hp.hpl.jena.rdf.model.Model;
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
public class MinPlanTest {
    
    private static Model model;
    
    public MinPlanTest(){
    }
    
    @Test(timeout=200)
    public void testInit() {
        MinPlan minPlan = new MinPlan(model);
        assertNotNull(minPlan.goals);
        assertTrue(!minPlan.goals.isEmpty());
        assertEquals(minPlan.goals, Query.toSet(Query.named("goals").execute(model), "g", Goal.class));
        assertNotNull(minPlan.plans);
        for(Set<Action> plan : minPlan.plans.values())
        {
            assertTrue(!plan.isEmpty());
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
    @Test(timeout=200)
    public void testToString() {
        System.out.println("toString");
        MinPlan minPlan = new MinPlan(model);
        String expResult = "Goal g:clean\n" +
"[a:put, a:go, a:grab, a:clean]Goal g:cook\n" +
"[a:put, a:go, a:grab]Goal g:watch_tv\n" +
"[a:go, a:switch, a:sit_down]";
        String result = minPlan.toString();
        assertEquals(expResult, result);
    }
    
}
