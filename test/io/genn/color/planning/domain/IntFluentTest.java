/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.genn.color.planning.domain;

import io.genn.color.planning.domain.IntFluent;
import org.junit.*;

import java.util.Set;

import static me.grea.antoine.utils.Collections.set;
import static org.junit.Assert.*;

/**
 *
 * @author antoine
 */
public class IntFluentTest {

    private IntFluent instance;
    private IntFluent nope;
    private IntFluent negator;
    private IntFluent notme;
    private IntFluent nnotme;
    private IntFluent another;
    private Set<IntFluent> all;
    
    public IntFluentTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        instance = new IntFluent(42);
        another = new IntFluent(instance);
        nope = new IntFluent(0);
        negator = new IntFluent(-42);
        notme = new IntFluent(7);
        nnotme = new IntFluent(-7);
        all = set(instance, another, nope, negator, notme, nnotme);
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of unifies method, of class IntFluent.
     */
    @Test
    public void testUnifies() {
        System.out.println("unifies");
        assertTrue(instance.unifies(instance));
        assertTrue(instance.unifies(another));
        assertTrue(another.unifies(instance));
        assertFalse(another.unifies(nope));
        assertFalse(nope.unifies(another));
        assertFalse(negator.unifies(another));
        assertFalse(instance.unifies(negator));
        assertFalse(instance.unifies(notme));
        assertFalse(instance.unifies(nnotme));
        assertFalse(notme.unifies(nnotme));
        assertFalse(nnotme.unifies(notme));
    }

    /**
     * Test of contradicts method, of class IntFluent.
     */
    @Test
    public void testContradicts() {
        System.out.println("contradicts");
        assertFalse(instance.contradicts(instance));
        assertFalse(instance.contradicts(another));
        assertFalse(another.contradicts(instance));
        assertFalse(another.contradicts(nope));
        assertTrue(nope.contradicts(nope)); // ?
        assertFalse(nope.contradicts(another));
        assertTrue(negator.contradicts(another));
        assertTrue(instance.contradicts(negator));
        assertFalse(instance.contradicts(notme));
        assertFalse(instance.contradicts(nnotme));
        assertTrue(notme.contradicts(nnotme));
        assertTrue(nnotme.contradicts(notme));
    }

    /**
     * Test of instanciate method, of class IntFluent.
     */
    @Test
    public void testInstanciate() {
        System.out.println("instanciate");
        for (IntFluent intFluent : all) {
            assertEquals(intFluent, intFluent.instanciate(intFluent));
        }
    }

    /**
     * Test of hashCode method, of class IntFluent.
     */
    @Test
    public void testHashCode() {
        System.out.println("hashCode");
        for (IntFluent intFluent : all) {
            assertEquals(intFluent.value, intFluent.hashCode());
        }
    }

    /**
     * Test of equals method, of class IntFluent.
     */
    @Test
    public void testEquals() {
        System.out.println("equals");
        assertEquals(instance, another);
        assertEquals(new IntFluent(42), another);
        assertNotEquals(instance, negator);
        assertNotEquals(nope, negator);
    }

    /**
     * Test of negate method, of class IntFluent.
     */
    @Test
    public void testNegate() {
        System.out.println("negate");
        for (IntFluent intFluent : all) {
            assertEquals(intFluent.negate().value, -intFluent.value);
        }
    }

    /**
     * Test of negative method, of class IntFluent.
     */
    @Test
    public void testNegative() {
        System.out.println("negative");
        for (IntFluent intFluent : all) {
            assertEquals(intFluent.value <0 , intFluent.negative());
        }
    }
    
}
