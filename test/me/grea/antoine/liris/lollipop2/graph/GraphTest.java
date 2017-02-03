/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.grea.antoine.liris.lollipop2.graph;

import org.junit.*;

import java.util.Arrays;
import java.util.Set;

import static me.grea.antoine.utils.Collections.set;
import static org.junit.Assert.*;

/**
 *
 * @author antoine
 */
public class GraphTest {

    private Graph<String, TestEdge> instance;
    private Set<TestEdge> edges;

    public GraphTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        instance = new Graph(TestEdge.class);
        edges = set(
                new TestEdge("b", "a"),
                new TestEdge("e", "a"),
                new TestEdge("a", "d"),
                new TestEdge("e", "c"),
                new TestEdge("a", "k"),
                new TestEdge("k", "d"),
                new TestEdge("d", "c"),
                new TestEdge("f", "g"),
                new TestEdge("h", "h"),
                new TestEdge("a", "c")
        );
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of addEdge method, of class Graph.
     */
    @Test(expected = NullPointerException.class)
    public void testAddEdge() {
        System.out.println("addEdge");
        TestEdge edge;
        assertNotNull(edge = instance.addEdge(new TestEdge("a", "b")));
        assertTrue(instance.containsVertex("a"));
        assertTrue(instance.containsVertex("b"));
        assertTrue(instance.containsEdge(edge));
        assertTrue(instance.containsEdge("a", "b"));

        assertNotNull(edge = instance.addEdge("a", "c"));
        assertTrue(instance.containsVertex("a"));
        assertTrue(instance.containsVertex("c"));
        assertTrue(instance.containsEdge(edge));
        assertTrue(instance.containsEdge("a", "c"));

        instance.addEdge(null, null);
        instance.addEdge(null);
    }

    /**
     * Test of addVertex method, of class Graph.
     */
    @Test
    public void testAddVertex() {
        System.out.println("addVertex");
        assertTrue(instance.vertexSet().isEmpty());
        assertTrue(instance.addVertex("a"));
        assertEquals(instance.vertexSet().size(), 1);
        assertTrue(instance.containsVertex("a"));
        assertTrue(instance.addVertex("b"));
        assertEquals(instance.vertexSet().size(), 2);
        assertTrue(instance.containsVertex("a"));
        assertTrue(instance.containsVertex("b"));
        assertFalse(instance.addVertex("b"));
        assertEquals(instance.vertexSet().size(), 2);
        assertTrue(instance.containsVertex("a"));
        assertTrue(instance.containsVertex("b"));
    }

    /**
     * Test of containsEdge method, of class Graph.
     */
    @Test
    public void testContainsEdge_Vertex() {
        System.out.println("containsEdge V");
        assertFalse(instance.containsEdge("a", "b"));
        instance.addEdge("a", "b");
        assertTrue(instance.containsEdge("a", "b"));
        assertFalse(instance.containsEdge("b", "a"));
        assertFalse(instance.containsEdge("a", "a"));
        instance.removeEdge("a", "b");
        assertFalse(instance.containsEdge("a", "b"));
    }

    /**
     * Test of containsEdge method, of class Graph.
     */
    @Test
    public void testContainsEdge_Edge() {
        System.out.println("containsEdge E");
        assertFalse(instance.containsEdge(new TestEdge("a", "b")));
        TestEdge edge = instance.addEdge(new TestEdge("a", "b"));
        assertTrue(instance.containsEdge(edge));
        assertTrue(instance.containsEdge(new TestEdge("a", "b")));
        assertFalse(instance.containsEdge(new TestEdge("b", "b")));
        assertFalse(instance.containsEdge(new TestEdge("a", "a")));
        instance.removeEdge(edge);
        assertFalse(instance.containsEdge(edge));
        assertFalse(instance.containsEdge(new TestEdge("a", "b")));
    }

    /**
     * Test of containsVertex method, of class Graph.
     */
    @Test
    public void testContainsVertex() {
        System.out.println("containsVertex");
        instance.addVertex("a");
        assertTrue(instance.containsVertex("a"));
        instance.addVertex("b");
        assertTrue(instance.containsVertex("a"));
        assertTrue(instance.containsVertex("b"));
    }

    /**
     * Test of edgeSet method, of class Graph.
     */
    @Test
    public void testEdgeSet() {
        System.out.println("edgeSet");
        assertEquals(instance.edgeSet().size(), 0);
        instance.addEdge("a", "b");
        assertEquals(instance.edgeSet().size(), 1);
        instance.addEdge("a", "c");
        assertEquals(instance.edgeSet().size(), 2);
        instance.addEdge("c", "b");
        assertEquals(instance.edgeSet().size(), 3);
        assertEquals(instance.edgeSet(), set(new TestEdge("a", "b"), new TestEdge("a", "c"), new TestEdge("c", "b")));
    }

    /**
     * Test of edgesOf method, of class Graph.
     */
    @Test
    public void testEdgesOf() {
        System.out.println("edgesOf");
        assertEquals(instance.edgesOf("a"), null);
        instance.addEdge("a", "b");
        assertEquals(set(new TestEdge("a", "b")), instance.edgesOf("a"));
        instance.addEdge("c", "d");
        assertEquals(set(new TestEdge("a", "b")), instance.edgesOf("a"));
        instance.addEdge("c", "a");
        assertEquals(set(new TestEdge("a", "b"), new TestEdge("c", "a")), instance.edgesOf("a"));
    }

    /**
     * Test of removeAllEdges method, of class Graph.
     */
    @Test
    public void testRemoveAllEdges() {
        System.out.println("removeAllEdges");
        assertEquals(instance.edgeSet().size(), 0);
        instance.addEdge("a", "b");
        instance.addEdge("a", "c");
        instance.addEdge("c", "b");
        assertEquals(instance.edgeSet().size(), 3);
        assertFalse(instance.removeAllEdges(set()));
        assertEquals(instance.edgeSet().size(), 3);
        assertTrue(instance.removeAllEdges(set(new TestEdge("a", "b"), new TestEdge("a", "c"), new TestEdge("c", "d"))));
        assertEquals(instance.edgeSet().size(), 1);
    }

    /**
     * Test of removeAllVertices method, of class Graph.
     */
    @Test
    public void testRemoveAllVertices() {
        System.out.println("removeAllVertices");
        assertEquals(instance.edgeSet().size(), 0);
        instance.addEdge("a", "b");
        instance.addEdge("a", "c");
        instance.addEdge("c", "b");
        assertEquals(instance.vertexSet().size(), 3);
        assertFalse(instance.removeAllVertices(set()));
        assertEquals(instance.vertexSet().size(), 3);
        assertTrue(instance.removeAllVertices(set("a", "b")));
        assertEquals(instance.vertexSet().size(), 1);
        assertEquals(instance.vertexSet(), set("c"));
    }

    /**
     * Test of edges method, of class Graph.
     */
    @Test
    public void testEdges() {
        System.out.println("edges");
        assertEquals(instance.edgeSet().size(), 0);
        instance.addEdge("a", "b");
        instance.addEdge("a", "c");
        instance.addEdge("c", "b");
        assertEquals(instance.edges("c", "a"), set());
        assertEquals(instance.edges("a", "b"), set(new TestEdge("a", "b")));
        assertEquals(instance.edges("a", null), set(new TestEdge("a", "b"), new TestEdge("a", "c")));
        assertEquals(instance.edges(null, "b"), set(new TestEdge("a", "b"), new TestEdge("c", "b")));
    }

    /**
     * Test of edge method, of class Graph.
     */
    @Test
    public void testEdge() {
        System.out.println("edge");
        assertEquals(instance.edgeSet().size(), 0);
        instance.addEdge("a", "b");
        instance.addEdge("a", "c");
        instance.addEdge("c", "b");
        assertNull(instance.edge("c", "a"));
        assertEquals(instance.edge("a", "b"), new TestEdge("a", "b"));
        assertEquals(instance.edge("a", "b"), new TestEdge("a", "b"));
        assertNull(instance.edge("a", null));
        instance.removeEdge("c", "b");
        assertNull(instance.edge("c", "b"));
     }

    /**
     * Test of removeEdge method, of class Graph.
     */
    @Test
    public void testRemoveEdge() {
        System.out.println("removeEdge");
        assertEquals(instance.edgeSet().size(), 0);
        instance.addEdge("a", "b");
        instance.addEdge("a", "c");
        instance.addEdge("c", "b");
        assertNull(instance.edge("c", "a"));
        assertEquals(new TestEdge("c", "b"), instance.removeEdge("c", "b"));
        assertFalse(instance.removeEdge(new TestEdge("c", "a")));
        assertTrue(instance.removeEdge(new TestEdge("a", "c")));
        assertTrue(instance.removeEdge(new TestEdge("a", "b")));
        assertEquals(instance.edgeSet().size(), 0);
    }
    
    /**
     * Test of removeVertex method, of class Graph.
     */
    @Test
    public void testRemoveVertex() {
        System.out.println("removeVertex");
        assertEquals(instance.edgeSet().size(), 0);
        instance.addEdge("a", "b");
        instance.addEdge("a", "c");
        instance.addEdge("c", "b");
        assertFalse(instance.removeVertex("x"));
        assertTrue(instance.removeVertex("a"));
    }

    /**
     * Test of vertexSet method, of class Graph.
     */
    @Test
    public void testVertexSet() {
        System.out.println("vertexSet");
        assertEquals(instance.edgeSet().size(), 0);
        instance.addEdge("a", "b");
        instance.addEdge("a", "c");
        instance.addEdge("c", "b");
        assertEquals(set("a", "b", "c"), instance.vertexSet());
    }

    /**
     * Test of inDegreeOf method, of class Graph.
     */
    @Test
    public void testInDegreeOf() {
        System.out.println("inDegreeOf");
        assertEquals(instance.edgeSet().size(), 0);
        instance.addEdge("a", "b");
        instance.addEdge("a", "c");
        instance.addEdge("c", "b");
        assertEquals(2, instance.inDegreeOf("b"));
        assertEquals(0, instance.inDegreeOf("z"));
        assertEquals(0, instance.inDegreeOf("a"));
        assertEquals(1, instance.inDegreeOf("c"));
    }

    /**
     * Test of incomingEdgesOf method, of class Graph.
     */
    @Test
    public void testIncomingEdgesOf() {
        System.out.println("incomingEdgesOf");
        assertEquals(instance.edgeSet().size(), 0);
        instance.addEdge("a", "b");
        instance.addEdge("a", "c");
        instance.addEdge("c", "b");
        assertEquals(set(new TestEdge("a", "b"), new TestEdge("c", "b")), instance.incomingEdgesOf("b"));
        assertEquals(set(), instance.incomingEdgesOf("z"));
        assertEquals(set(), instance.incomingEdgesOf("a"));
        assertEquals(set(new TestEdge("a", "c")), instance.incomingEdgesOf("c"));
    }

    /**
     * Test of outDegreeOf method, of class Graph.
     */
    @Test
    public void testOutDegreeOf() {
        System.out.println("outDegreeOf");
        assertEquals(instance.edgeSet().size(), 0);
        instance.addEdge("a", "b");
        instance.addEdge("a", "c");
        instance.addEdge("c", "b");
        assertEquals(0, instance.outDegreeOf("b"));
        assertEquals(0, instance.outDegreeOf("z"));
        assertEquals(2, instance.outDegreeOf("a"));
        assertEquals(1, instance.outDegreeOf("c"));
    }

    /**
     * Test of outgoingEdgesOf method, of class Graph.
     */
    @Test
    public void testOutgoingEdgesOf() {
        System.out.println("outgoingEdgesOf");
        assertEquals(instance.edgeSet().size(), 0);
        instance.addEdge("a", "b");
        instance.addEdge("a", "c");
        instance.addEdge("c", "b");
        assertEquals(set(), instance.outgoingEdgesOf("b"));
        assertEquals(set(), instance.outgoingEdgesOf("z"));
        assertEquals(set(new TestEdge("a", "b"), new TestEdge("a", "c")), instance.outgoingEdgesOf("a"));
        assertEquals(set(new TestEdge("c", "b")), instance.outgoingEdgesOf("c"));
    }

    /**
     * Test of antiCyclicity
     */
    @Test
    public void testCyclicity() {
        System.out.println("cycles");
        boolean[] passed = new boolean[2];
        Arrays.fill(passed, false);
        try {
            instance.addEdge("a", "a");
            fail("Cycle undetected !");
        } catch (IllegalStateException e) {
            try {
                instance.addEdge("a", "b");
                instance.addEdge("b", "c");
                passed[0]=true;
                instance.addEdge("b", "a");
                fail("Cycle undetected !");
            } catch (IllegalStateException e1) {
                assertTrue(passed[0]);
                try {
                    instance.addEdge("c", "a");
                    fail("Cycle undetected !");
                } catch (IllegalStateException e2) {
                    try {
                    instance.removeEdge("b", "c");
                    instance.addEdge("c", "a");
                    instance.addEdge("c", "b"); //shouldn't throw
                    instance.addEdge("b", "e"); //shouldn't throw
                    passed[1]=true;
                    instance.addEdge("e", "c"); 
                    fail("Cycle undetected !");
                    } catch (IllegalStateException e3) {
                        assertTrue(passed[1]);
                    }
                }
            }
        }
    }

    /**
     * Test of reachable method, of class Graph.
     */
    @Test
    public void testReachable() {
        System.out.println("reachable");
        instance.addEdge("a", "c");
        instance.addEdge("c", "b");
        assertFalse(instance.reachable("b", "c"));
        assertTrue(instance.reachable("a", "c"));
        assertTrue(instance.reachable("c", "b"));
        assertTrue(instance.reachable("a", "b"));
        instance.removeEdge("c", "b");
        assertTrue(instance.reachable("a", "c"));
        assertFalse(instance.reachable("c", "b"));
        assertFalse(instance.reachable("a", "b"));
        
    }

}
