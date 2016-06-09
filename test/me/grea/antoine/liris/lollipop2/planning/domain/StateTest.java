/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.liris.lollipop2.planning.domain;

import java.util.HashSet;
import java.util.Set;
import static me.grea.antoine.utils.Collections.set;
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
public class StateTest {

    private HashSet<IntFluent> pool;
    private State<IntFluent> instance;
    private State<IntFluent> empty;
    private State<IntFluent> closed;
    private State<IntFluent> modifiable;
    
    public StateTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        pool = new HashSet<>();
        Set<Integer> positives = Dice.roll(1, 20, 10);
        for (int fluent : positives ) {
            pool.add(new IntFluent(fluent));
        }
        for (int i = -1; i >= -20; i--) {
            if (!positives.contains(-i)) {
                pool.add(new IntFluent(i));
            }
        }
        instance = new State<>(pool);
        closed = new State<>(set(new IntFluent(17), new IntFluent(-15)), true);
        modifiable = new State<>(set(new IntFluent(17), new IntFluent(-15)), false, false);
        empty = new State<>();
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of add method, of class State.
     */
    @Test
    public void testAdd() {
        System.out.println("add");
        for (IntFluent intFluent : pool) {
            assertFalse(instance.add(intFluent));
            assertEquals(instance.size(), pool.size());
            assertFalse(closed.add(intFluent));
            assertEquals(closed.size(), 2);
            assertFalse(empty.add(intFluent));
            assertEquals(empty.size(), 0);
        }
        assertFalse(modifiable.add(new IntFluent(-17)));
        assertFalse(modifiable.add(new IntFluent(15)));
        assertTrue(modifiable.add(new IntFluent(5)));
        assertEquals(modifiable.size(), 3);
        assertTrue(modifiable.add(new IntFluent(-2)));
        assertEquals(modifiable.size(), 4);
        assertFalse(modifiable.add(new IntFluent(-5)));
        assertFalse(modifiable.add(new IntFluent(2)));
        assertEquals(modifiable.size(), 4);
    }

    /**
     * Test of contains method, of class State.
     */
    @Test
    public void testContains() {
        System.out.println("contains");
        for (IntFluent intFluent : pool) {
            assertTrue(instance.contains(intFluent));
            assertFalse(instance.contains(intFluent.negate()));
            assertFalse(empty.contains(intFluent));
        }
        assertFalse(closed.contains(new IntFluent(-17)));
        assertFalse(closed.contains(new IntFluent(42)));
        assertFalse(closed.contains(new IntFluent(2)));
        assertTrue(closed.contains(new IntFluent(-15)));
        assertTrue(closed.contains(new IntFluent(-1)));
        assertTrue(closed.contains(new IntFluent(-2)));
        assertTrue(closed.contains(new IntFluent(-51)));
    }

    /**
     * Test of clear method, of class State.
     */
    @Test
    public void testClear() {
        System.out.println("clear");
        assertEquals(instance.size(), pool.size());
        instance.clear();
        assertEquals(instance.size(), pool.size());
        assertEquals(modifiable.size(), 2);
        modifiable.clear();
        assertEquals(modifiable.size(), 0);
    }

    /**
     * Test of remove method, of class State.
     */
    @Test
    public void testRemove() {
        System.out.println("remove");
        assertEquals(instance.size(), pool.size());
        instance.clear();
        assertEquals(instance.size(), pool.size());
        assertEquals(modifiable.size(), 2);
        modifiable.clear();
        assertEquals(modifiable.size(), 0);
    }

    /**
     * Test of contradicts method, of class State.
     */
    @Test
    public void testContradicts() {
        System.out.println("contradicts");
        for (IntFluent intFluent : pool) {
            assertFalse(instance.contradicts(intFluent));
            assertTrue(instance.contradicts(intFluent.negate()));
            assertFalse(empty.contradicts(intFluent));
        }
        assertTrue(closed.contradicts(new IntFluent(-17)));
        assertFalse(closed.contradicts(new IntFluent(-18)));
        assertFalse(closed.contradicts(new IntFluent(-15)));
        assertFalse(closed.contradicts(new IntFluent(17)));
        assertTrue(closed.contradicts(new IntFluent(15)));
        assertTrue(closed.contradicts(new IntFluent(1)));
        assertTrue(closed.contradicts(new IntFluent(42)));
    }

    /**
     * Test of unifies method, of class State.
     */
    @Test
    public void testUnifies() {
        System.out.println("unifies");
        for (IntFluent intFluent : pool) {
            assertTrue(instance.unifies(intFluent));
            assertFalse(instance.unifies(intFluent.negate()));
            assertFalse(empty.unifies(intFluent));
        }
        assertFalse(closed.unifies(new IntFluent(-17)));
        assertTrue(closed.unifies(new IntFluent(-18)));
        assertTrue(closed.unifies(new IntFluent(-15)));
        assertTrue(closed.unifies(new IntFluent(17)));
        assertFalse(closed.unifies(new IntFluent(15)));
        assertFalse(closed.unifies(new IntFluent(1)));
        assertFalse(closed.unifies(new IntFluent(42)));
    }
    
}
