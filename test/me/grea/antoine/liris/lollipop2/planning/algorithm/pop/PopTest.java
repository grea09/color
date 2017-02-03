/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.liris.lollipop2.planning.algorithm.pop;

import me.grea.antoine.liris.lollipop2.parsing.ProblemParser;
import me.grea.antoine.liris.lollipop2.planning.domain.IntFluent;
import me.grea.antoine.liris.lollipop2.planning.problem.Problem;
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
