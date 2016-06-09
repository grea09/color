/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.liris.lollipop2.planning.flaw;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import me.grea.antoine.liris.lollipop2.parsing.ProblemParser;
import me.grea.antoine.liris.lollipop2.planning.domain.Action;
import me.grea.antoine.liris.lollipop2.planning.domain.IntFluent;
import me.grea.antoine.liris.lollipop2.planning.domain.State;
import me.grea.antoine.liris.lollipop2.planning.problem.Plan;
import me.grea.antoine.liris.lollipop2.planning.problem.Problem;
import static me.grea.antoine.utils.Collections.set;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author antoine
 */
public class ResolverTest {

    private Problem<IntFluent> problem;
    private Action<IntFluent> action;
    private Resolver normal;
    private Resolver reverse;
    private Resolver negative;
    private Resolver rnegative;
    private Resolver corrupted;

    public ResolverTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws IOException {
        problem = ProblemParser.parse(new File("data/sample.w"));
        action = new Action<>(new State<>(new IntFluent(1)), new State<>(new IntFluent(3), new IntFluent(5)), Action.Flag.NORMAL);
        normal = new Resolver(action, problem.goal, new IntFluent(3));
        reverse = new Resolver(problem.goal,action);
        negative = new Resolver(action, problem.goal, new IntFluent(3), true);
        rnegative = new Resolver(problem.goal,action, null, true);
        corrupted = new Resolver(action, problem.goal, new IntFluent(-18));
    }

    @After
    public void tearDown() {
        problem.plan = new Plan<>();
    }

    /**
     * Test of apply method, of class Resolver.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testApply() {
        System.out.println("apply");
        normal.apply(problem.plan);
        assertTrue(problem.plan.containsEdge(action, problem.goal));
        assertTrue(problem.plan.edge(action, problem.goal).causes.contains(new IntFluent(3)));
        try {
            reverse.apply(problem.plan);
            fail("Cycles should be prevented !");
        } catch (IllegalStateException e)
        {
            
        }
        assertTrue(problem.plan.containsEdge(action, problem.goal));
        assertFalse(problem.plan.containsEdge(problem.goal, action));
        rnegative.apply(problem.plan);
        assertTrue(problem.plan.containsEdge(action, problem.goal));
        negative.apply(problem.plan);
        assertFalse(problem.plan.containsEdge(action, problem.goal));
        corrupted.apply(problem.plan);
    }

    /**
     * Test of appliable method, of class Resolver.
     */
    @Test
    public void testAppliable() {
        System.out.println("appliable");
        assertTrue(normal.appliable(problem.plan));
        assertTrue(reverse.appliable(problem.plan));
        assertFalse(negative.appliable(problem.plan));
        assertFalse(rnegative.appliable(problem.plan));
        assertFalse(corrupted.appliable(problem.plan));
        normal.apply(problem.plan);
        assertTrue(normal.appliable(problem.plan));
        assertFalse(reverse.appliable(problem.plan));
        assertTrue(negative.appliable(problem.plan));
        assertFalse(rnegative.appliable(problem.plan));
        assertFalse(corrupted.appliable(problem.plan));
    }

    /**
     * Test of revert method, of class Resolver.
     */
    @Test
    public void testRevert() {
        System.out.println("revert");
        normal.apply(problem.plan);
        assertTrue(problem.plan.containsEdge(action, problem.goal));
        normal.revert();
        assertFalse(problem.plan.containsEdge(action, problem.goal));
        assertTrue(problem.plan.containsVertex(problem.goal));
        assertFalse(problem.plan.containsVertex(action));
        
        normal.apply(problem.plan);
        assertTrue(problem.plan.containsEdge(action, problem.goal));
        negative.apply(problem.plan);
        assertFalse(problem.plan.containsEdge(action, problem.goal));
        negative.revert();
        assertTrue(problem.plan.containsEdge(action, problem.goal));
        normal.revert();
        assertFalse(problem.plan.containsEdge(action, problem.goal));
        
        normal.apply(problem.plan);
        assertTrue(problem.plan.containsEdge(action, problem.goal));
        assertTrue(problem.plan.edge(action, problem.goal).causes.contains(new IntFluent(3)));
        negative.apply(problem.plan);
        assertFalse(problem.plan.containsEdge(action, problem.goal));
        normal.revert();
        assertFalse(problem.plan.containsEdge(action, problem.goal));
        negative.revert();
        assertTrue(problem.plan.containsEdge(action, problem.goal));
        normal.revert();
        assertFalse(problem.plan.containsEdge(action, problem.goal));
    }

}
