/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.genn.color.planning.algorithm.pop;

import io.genn.color.parsing.ProblemParser;
import io.genn.color.planning.algorithm.pop.Pop;
import io.genn.color.planning.domain.IntFluent;
import io.genn.color.planning.problem.Problem;
import org.junit.*;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

/**
 *
 * @author antoine
 */
public class PopTest {

    private Problem<IntFluent> problem;
    private Pop pop;
    
    public PopTest() {
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
        pop = new Pop(problem);
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of solve method, of class Pop.
     */
    @Test
    public void testSolve() throws Exception {
        System.out.println("solve");
        pop.solve();
        assertTrue(problem.solved());
    }
    
}
