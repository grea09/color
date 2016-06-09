/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.liris.lollipop2.graph;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
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
public class DirectedEdgesTest {

    private Set<TestEdge> edges;
    private DirectedEdges instance;

    public DirectedEdgesTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        edges = set(
                new TestEdge("b", "a"),
                new TestEdge("e", "a"),
                new TestEdge("a", "d"),
                new TestEdge("e", "c"),
                new TestEdge("a", "c")
        );
        instance = new DirectedEdges(edges, "a");
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of contains method, of class DirectedEdges.
     */
    @Test
    public void testContains() {
        System.out.println("contains");
        assertFalse(instance.contains(new TestEdge("a", "a")));
        assertFalse(instance.contains(new TestEdge("a", "b")));
        assertFalse(instance.contains(new TestEdge("f", "b")));
        assertFalse(instance.contains(new TestEdge("d", "a")));
        assertTrue(instance.contains(new TestEdge("b", "a")));
        assertTrue(instance.contains(new TestEdge("a", "c")));
    }

    /**
     * Test of hashCode method, of class DirectedEdges.
     */
    @Test
    public void testHashCode() {
        System.out.println("hashCode");
        int expResult = 1131361;
        int result = instance.hashCode();
        assertEquals(expResult, result);
    }

    /**
     * Test of equals method, of class DirectedEdges.
     */
    @Test
    public void testEquals() {
        System.out.println("equals");
        DirectedEdges<String, TestEdge> copy = new DirectedEdges<>(instance);
        Set<TestEdge> collection = new HashSet<>(instance);
        DirectedEdges<String, TestEdge> same = new DirectedEdges<>(edges, "a");
        DirectedEdges<String, TestEdge> different = new DirectedEdges<>(edges, "b");
        DirectedEdges<String, TestEdge> different2 = new DirectedEdges<>(set(), "a");
        assertEquals(instance, instance);
        assertEquals(instance, collection);
        assertEquals(collection, instance);
        assertEquals(instance, copy);
        assertEquals(copy, instance);
        assertEquals(instance, same);
        assertEquals(same, instance);
        assertNotEquals(instance, different);
        assertNotEquals(different, instance);
        assertNotEquals(instance, different2);
        assertNotEquals(different2, instance);
        assertNotEquals(different2, null);
        assertNotEquals(instance, null);
        assertNotEquals(instance, new DirectedEdges<>("a"));
    }

    /**
     * Test of size method, of class DirectedEdges.
     */
    @Test
    public void testSize() {
        System.out.println("size");
        assertEquals(new DirectedEdges<>("a").size(), 0);
        assertEquals(instance.size(), edges.size()-1);
        instance.add(new TestEdge("a", "a"));
        assertEquals(instance.size(), edges.size());
        instance.clear();
        assertEquals(instance.size(), 0);
    }

    /**
     * Test of isEmpty method, of class DirectedEdges.
     */
    @Test
    public void testIsEmpty() {
        System.out.println("isEmpty");
        assertFalse(instance.isEmpty());
        instance.clear();
        assertTrue(instance.isEmpty());
        assertTrue(new DirectedEdges<String, TestEdge>("a").isEmpty());
    }

    /**
     * Test of iterator method, of class DirectedEdges.
     */
    @Test
    public void testIterator() {
        System.out.println("iterator");
        int i = 0;
        for (Iterator<TestEdge> iterator = instance.iterator(); iterator.hasNext();) {
            TestEdge next = iterator.next();
            assertTrue(instance.contains(next));
            i++;
        }
        assertEquals(instance.size(), i);
    }

    /**
     * Test of toArray method, of class DirectedEdges.
     */
    @Test
    public void testToArray_0args() {
        System.out.println("toArray");
        edges.remove(new TestEdge("e", "c"));
        assertArrayEquals(instance.toArray(), edges.toArray());
    }

    /**
     * Test of toArray method, of class DirectedEdges.
     */
    @Test
    public void testToArray_GenericType() {
        System.out.println("toArray T");
        TestEdge[] array = new TestEdge[1];
        edges.remove(new TestEdge("e", "c"));
        assertArrayEquals(instance.toArray(array), edges.toArray(array));
    }

    /**
     * Test of add method, of class DirectedEdges.
     */
    @Test
    public void testAdd() {
        System.out.println("add");
        DirectedEdges copy = new DirectedEdges<>(instance);
        copy.add(new TestEdge("e", "c"));
        assertEquals(instance, copy);
        copy.add(new TestEdge("a", "b"));
        assertEquals(instance.size() + 1, copy.size());
        copy.add(new TestEdge("a", "b"));
        copy.add(new TestEdge("b", "a"));
        assertEquals(instance.size() + 1, copy.size());
        copy.add(new TestEdge("a", "a"));
        assertEquals(instance.size() + 2, copy.size());
    }

    /**
     * Test of remove method, of class DirectedEdges.
     */
    @Test
    public void testRemove() {
        System.out.println("remove");
        int size = instance.size();
        for (TestEdge edge : edges) {
            if (instance.contains(edge)) {
                assertTrue(instance.remove(edge));
                assertEquals(instance.size(), --size);
            }
        }
        assertFalse(instance.remove(new TestEdge("b", "a")));
        assertFalse(instance.remove(null));
    }

    /**
     * Test of containsAll method, of class DirectedEdges.
     */
    @Test
    public void testContainsAll() {
        System.out.println("containsAll");
        assertFalse(instance.containsAll(edges));
        assertFalse(instance.containsAll(set(new TestEdge("a", "a"))));
        assertTrue(instance.containsAll(set()));
        instance.contains(null);
    }

    /**
     * Test of addAll method, of class DirectedEdges.
     */
    @Test(expected=NullPointerException.class)
    public void testAddAll() {
        System.out.println("addAll");
        DirectedEdges copy = new DirectedEdges<>(instance);
        assertFalse(copy.addAll(edges));
        assertEquals(instance, copy);
        assertTrue(copy.addAll(set(new TestEdge("a", "a"), new TestEdge("c", "a"))));
        assertEquals(instance.size()+2, copy.size());
        copy.addAll(null);
    }

    /**
     * Test of retainAll method, of class DirectedEdges.
     */
    @Test(expected=NullPointerException.class)
    public void testRetainAll() {
        System.out.println("retainAll");
        int size = instance.size();
        assertFalse(instance.retainAll(edges));
        assertEquals(instance.size(), size);
        assertTrue(instance.retainAll(set(new TestEdge("b", "a"), new TestEdge("e", "c"))));
        assertEquals(instance.size(), 1);
        instance.retainAll(null);
    }

    /**
     * Test of removeAll method, of class DirectedEdges.
     */
    @Test(expected=NullPointerException.class)
    public void testRemoveAll() {
        System.out.println("removeAll");
        int size = instance.size();
        assertFalse(instance.removeAll(set()));
        assertEquals(instance.size(), size);
        assertTrue(instance.removeAll(set(new TestEdge("b", "a"), new TestEdge("e", "c"))));
        assertEquals(instance.size(), size-1);
        assertTrue(instance.removeAll(edges));
        assertEquals(instance.size(), 0);
        instance.removeAll(null);
    }

    /**
     * Test of clear method, of class DirectedEdges.
     */
    @Test
    public void testClear() {
        System.out.println("clear");
        instance.clear();
        assertEquals(instance.size(), 0);
    }

}
