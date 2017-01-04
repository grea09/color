/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.genn.color.planning.algorithm.pop;

import io.genn.color.parsing.ProblemParser;
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
public class PopSubGoalTest {

    private Problem<IntFluent> problem;
    private Set<PopSubGoal<IntFluent>> flaws;
    private PopSubGoal<IntFluent> subgoal;
    private Action<IntFluent> action1;
    private Action<IntFluent> action2;
    private Deque<Resolver<IntFluent>> resolvers;
    
    public PopSubGoalTest() {
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
        subgoal = new PopSubGoal<>(new IntFluent(3), problem.goal, problem);
        flaws = set(
                subgoal,
                new PopSubGoal<>(new IntFluent(4), problem.goal, problem),
                new PopSubGoal<>(new IntFluent(-5), problem.goal, problem),
                new PopSubGoal<>(new IntFluent(6), problem.goal, problem)
        );
        action1 = new Action<>(new State<>(new IntFluent(1)), new State<>(new IntFluent(3), new IntFluent(5)), Action.Flag.NORMAL);
        action2 = new Action<>(new State<>(new IntFluent(9)), new State<>(new IntFluent(3)), Action.Flag.NORMAL);
        resolvers = queue(
                new Resolver<>(action2, problem.goal, new IntFluent(3)),
                new Resolver<>(action1, problem.goal, new IntFluent(3))
        );
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of resolvers method, of class PopSubGoal.
     */
    @Test
    public void testResolvers() {
        System.out.println("resolvers");
        assertEquals(subgoal.resolvers().size(), 2);
//        assertEquals(subgoal.resolvers(), resolvers);
    }

    /**
     * Test of related method, of class PopSubGoal.
     */
    @Test
    public void testRelated_Resolver() {
        System.out.println("related");
        Resolver<IntFluent> resolver = new Resolver<>(action1, problem.goal, new IntFluent(3));
        assertTrue(new PopSubGoal<>(problem).related(resolver).isEmpty());
        resolver.apply(problem.plan);
        assertFalse(new PopSubGoal<>(problem).related(resolver).isEmpty());
        resolver.revert();
    }

    /**
     * Test of related method, of class PopSubGoal.
     */
    @Test
    public void testRelated_Action() {
        System.out.println("related");
        assertEquals(flaws, new PopSubGoal<>(problem).related(problem.goal));
        assertTrue(new PopSubGoal<>(problem).related(problem.initial).isEmpty());
        assertTrue(new PopSubGoal<>(problem).related(action1).isEmpty());
        assertTrue(new PopSubGoal<>(problem).related(action2).isEmpty());
        Resolver<IntFluent> resolver = new Resolver<>(action1, problem.goal, new IntFluent(3));
        resolver.apply(problem.plan);
        assertFalse(new PopSubGoal<>(problem).related(action1).isEmpty());
        resolver.revert();
    }

    /**
     * Test of flaws method, of class PopSubGoal.
     */
    @Test
    public void testFlaws() {
        System.out.println("flaws");
        assertEquals(flaws, new PopSubGoal<>(problem).flaws());
    }

    /**
     * Test of invalidated method, of class PopSubGoal.
     */
    @Test
    public void testInvalidated() {
        System.out.println("invalidated");
        for (Resolver<IntFluent> resolver : resolvers) {
            assertTrue(subgoal.invalidated(resolver));
        }
        assertFalse(subgoal.invalidated(new Resolver<>(action1, problem.goal, new IntFluent(5))));
        assertFalse(subgoal.invalidated(new Resolver<>(action1, action2, new IntFluent(3))));
    }
    
}
