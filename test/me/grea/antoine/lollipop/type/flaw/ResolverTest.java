/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.lollipop.type.flaw;

import java.util.HashSet;
import java.util.Set;
import me.grea.antoine.lollipop.agenda.Agenda;
import me.grea.antoine.lollipop.benchmark.ProblemGenerator;
import me.grea.antoine.lollipop.type.Action;
import me.grea.antoine.lollipop.type.Domain;
import me.grea.antoine.lollipop.type.Plan;
import me.grea.antoine.lollipop.type.Problem;
import me.grea.antoine.utils.Dice;
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

    private Plan plan;
    private Problem problem;
    private Set<Resolver> resolvers;
    private int actions;
    private int links;
    
    private static final int enthropy = 1000;
    private static final int hardness = (int) (enthropy*0.4);

    public ResolverTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        plan = new Plan();
        problem = new Problem(new Action(null, null), new Action(null, null), new Domain(), plan);
        Action seed = ProblemGenerator.provider(problem, enthropy, hardness, enthropy/2);
        seed.preconditions.add(enthropy/3);
        plan.addVertex(seed);
        resolvers = new HashSet<>();
        actions = plan.vertexSet().size();
        links = plan.edgeSet().size();
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of apply method, of class Resolver.
     */
    @Test
    public void testApply() {
        System.out.println("apply");
        for (int i = 0; i < enthropy; i++) {
            Action target = Dice.pick(plan.vertexSet());
            Integer fluent = target.preconditions.isEmpty() ? Dice.roll(1, enthropy) : Dice.pick(target.preconditions);
            Action source = ProblemGenerator.provider(problem, enthropy, hardness, fluent);
            source.preconditions.add(Dice.roll(1, enthropy));
            Resolver instance = new Resolver(source, target, fluent);
            instance.apply(plan);
            resolvers.add(instance);
            assertTrue(plan.containsEdge(source, target));
            assertTrue(plan.getEdge(source, target).labels.contains(fluent));

            Action threat = ProblemGenerator.provider(problem, enthropy, hardness, -fluent);
            while (plan.containsVertex(threat)) {
                threat = ProblemGenerator.provider(problem, enthropy, hardness, -fluent);
            }
            threat.preconditions.add(Dice.roll(1, enthropy));
            Resolver threatening = new Resolver(threat, target, 0);
            threatening.apply(plan);
            resolvers.add(threatening);
            assertTrue(plan.containsEdge(threat, target));
            assertTrue(plan.getEdge(threat, target).labels.isEmpty());
        }
    }

    /**
     * Test of revert method, of class Resolver.
     */
    @Test
    public void testRevert() {
        System.out.println("revert");
        for (Resolver resolver : resolvers) {
            resolver.revert(plan);
        }
        assertEquals(plan.vertexSet().size(), actions);
        assertEquals(plan.edgeSet().size(), links);
    }

    /**
     * Test of appliable method, of class Resolver.
     */
//    @Test
//    public void testAppliable() {
//        System.out.println("appliable");
//        Plan plan = null;
//        Resolver instance = null;
//        boolean expResult = false;
//        boolean result = instance.appliable(plan);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

}
