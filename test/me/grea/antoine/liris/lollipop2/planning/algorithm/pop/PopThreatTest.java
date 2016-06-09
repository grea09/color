/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.liris.lollipop2.planning.algorithm.pop;

import java.io.File;
import java.io.IOException;
import java.util.Deque;
import java.util.Set;
import me.grea.antoine.liris.lollipop2.parsing.ProblemParser;
import me.grea.antoine.liris.lollipop2.planning.domain.Action;
import me.grea.antoine.liris.lollipop2.planning.domain.IntFluent;
import me.grea.antoine.liris.lollipop2.planning.domain.State;
import me.grea.antoine.liris.lollipop2.planning.flaw.Resolver;
import me.grea.antoine.liris.lollipop2.planning.problem.Problem;
import static me.grea.antoine.utils.Collections.queue;
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
public class PopThreatTest {

    private Problem<IntFluent> problem;
    private Set<PopThreat<IntFluent>> flaws;
    private Action<IntFluent> action1;
    private Action<IntFluent> action2;
    private Deque<Resolver<IntFluent>> resolvers;
    private Deque<Resolver<IntFluent>> scenario;
    private PopThreat<IntFluent> threat;
    
    public PopThreatTest() {
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
        action1 = new Action<>(new State<>(new IntFluent(1)), new State<>(new IntFluent(3), new IntFluent(5)), Action.Flag.NORMAL);
        action2 = new Action<>(new State<>(new IntFluent(4)), new State<>(new IntFluent(-5)), Action.Flag.NORMAL);
        scenario = queue(
                new Resolver<>(action1, problem.goal, new IntFluent(3)),
                new Resolver<>(action2, problem.goal, new IntFluent(-5))
        );
        for (Resolver<IntFluent> resolver : scenario) {
            resolver.apply(problem.plan);
        }
        threat = new PopThreat<>(new IntFluent(-5), action1, problem.plan.edge(action2, problem.goal), problem);
        flaws = set(
                threat
        );
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of resolvers method, of class PopThreat.
     */
    @Test
    public void testResolvers() {
        System.out.println("resolvers");
        assertEquals(threat.resolvers().size(), 1);
    }

    /**
     * Test of flaws method, of class PopThreat.
     */
    @Test
    public void testFlaws() {
        System.out.println("flaws");
        assertEquals(flaws, new PopThreat<>(problem).flaws());
    }

    /**
     * Test of invalidated method, of class PopThreat.
     */
    @Test
    public void testInvalidated() {
        System.out.println("invalidated");
        assertTrue(threat.invalidated(new Resolver<>(action1, action2)));
        assertFalse(threat.invalidated(new Resolver<>(action2, action1)));
        assertFalse(threat.invalidated(new Resolver<>(action2, problem.goal)));
        assertFalse(threat.invalidated(new Resolver<>(action1, problem.goal)));
    }
    
}
