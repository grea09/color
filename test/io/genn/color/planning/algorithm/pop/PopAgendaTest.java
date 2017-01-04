/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.genn.color.planning.algorithm.pop;

import io.genn.color.parsing.ProblemParser;
import io.genn.color.planning.algorithm.pop.PopAgenda;
import io.genn.color.planning.algorithm.pop.PopSubGoal;
import io.genn.color.planning.domain.Action;
import io.genn.color.planning.domain.IntFluent;
import io.genn.color.planning.domain.State;
import io.genn.color.planning.flaw.Resolver;
import io.genn.color.planning.problem.Problem;
import org.junit.*;

import java.io.File;
import java.io.IOException;
import java.util.Deque;
import java.util.Set;

import static me.grea.antoine.utils.Collections.queue;
import static me.grea.antoine.utils.Collections.set;
import static org.junit.Assert.*;

/**
 *
 * @author antoine
 */
public class PopAgendaTest {

    private Problem<IntFluent> problem;
    private PopAgenda<IntFluent> agenda;
    private Set<PopSubGoal<IntFluent>> flaws;
    private Action<IntFluent> action1;
    private Action<IntFluent> action2;
    private Deque<Resolver<IntFluent>> scenario;
    
    public PopAgendaTest() {
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
        agenda = new PopAgenda<>(problem);
        flaws = set(
                new PopSubGoal<>(new IntFluent(3), problem.goal, problem),
                new PopSubGoal<>(new IntFluent(4), problem.goal, problem),
                new PopSubGoal<>(new IntFluent(-5), problem.goal, problem),
                new PopSubGoal<>(new IntFluent(6), problem.goal, problem)
        );
        
        action1 = new Action<>(new State<>(new IntFluent(1)), new State<>(new IntFluent(3), new IntFluent(5)), Action.Flag.NORMAL);
        action2 = new Action<>(new State<>(new IntFluent(4)), new State<>(new IntFluent(-5)), Action.Flag.NORMAL);
        scenario = queue(
                new Resolver<>(action1, problem.goal, new IntFluent(3)),
                new Resolver<>(action2, problem.goal, new IntFluent(-5))
        );
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of populate method, of class PopAgenda.
     */
    @Test
    public void testPopulate() {
        System.out.println("populate");
        assertTrue(flaws.containsAll(agenda));
    }
    
    /**
     * Test of choose method, of class PopAgenda.
     */
    @Test
    public void testChoose() {
        System.out.println("choose");
        assertNotNull(agenda.choose());
    }

    /**
     * Test of related method, of class PopAgenda.
     */
    @Test
    public void testRelated() {
        System.out.println("related");
        for (Resolver<IntFluent> resolver : scenario) {
            resolver.apply(problem.plan);
            agenda.related(resolver);
        }
        assertEquals(agenda.size(), 5);
        
    }
    
}
