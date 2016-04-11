/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.lollipop.algorithm;

import me.grea.antoine.lollipop.benchmark.ProblemGenerator;
import me.grea.antoine.lollipop.benchmark.SolutionChecker;
import me.grea.antoine.lollipop.type.Problem;
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
public class PartialOrderPlanningTest {
    
    public PartialOrderPlanningTest() {
    }
    
    @BeforeClass 
    public static void setUpClass() {
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
     * Test of solve method, of class PartialOrderPlanning.
     */
    @Test
    public void testSolve() {
        System.out.println("solve");
        Problem problem = (Problem) ProblemGenerator.generate(1, 8, 5).toArray()[0];
        boolean expResult = true;
        boolean result = PartialOrderPlanning.solve(problem);
        assertEquals(expResult, result);
        assertTrue("Problem validity", SolutionChecker.check(problem));
    }

    /**
     * Test of refine method, of class PartialOrderPlanning.
     */
    @Test
    public void testRefine() throws Exception {
        System.out.println("refine");
        Problem problem = (Problem) ProblemGenerator.generate(1, 18, 10).toArray()[0];
        PartialOrderPlanning instance = new PartialOrderPlanning(problem);
        int steps = problem.plan.vertexSet().size();
        int links = problem.plan.edgeSet().size();
        instance.refine();
        assertTrue("More Steps or Links", problem.plan.vertexSet().size() > steps || problem.plan.edgeSet().size() > links);
    }
    
}
