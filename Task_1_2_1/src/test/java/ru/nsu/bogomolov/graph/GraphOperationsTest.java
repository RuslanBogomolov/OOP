package ru.nsu.bogomolov.graph;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import ru.nsu.bogomolov.support.GraphType;

/** Проверяет общий договор интерфейса на всех способах хранения. */
class GraphOperationsTest {

    @ParameterizedTest
    @EnumSource(GraphType.class)
    void emptyGraph(GraphType type) {
        Graph graph = type.create();
        assertTrue(graph.getVertices().isEmpty());
        assertEquals("", graph.toString());
        graph.removeVertex(100);
        graph.clear();
        assertTrue(graph.getVertices().isEmpty());
    }

    @ParameterizedTest
    @EnumSource(GraphType.class)
    void arbitraryVertexNumbersAreSortedAndUnique(GraphType type) {
        Graph graph = type.create(100, -3, 0, 100, Integer.MAX_VALUE, Integer.MIN_VALUE);
        assertEquals(List.of(Integer.MIN_VALUE, -3, 0, 100, Integer.MAX_VALUE),
                graph.getVertices());
        assertTrue(graph.getNeighbors(100).isEmpty());
    }

    @ParameterizedTest
    @EnumSource(GraphType.class)
    void edgesHaveDirection(GraphType type) {
        Graph graph = type.create(10, 20, 30);
        graph.addEdge(10, 20);
        assertTrue(graph.hasEdge(10, 20));
        assertFalse(graph.hasEdge(20, 10));
        assertEquals(List.of(20), graph.getNeighbors(10));
        assertTrue(graph.getNeighbors(20).isEmpty());
        assertTrue(graph.getNeighbors(30).isEmpty());
    }

    @ParameterizedTest
    @EnumSource(GraphType.class)
    void repeatedAdditionPreservesEdges(GraphType type) {
        Graph graph = type.create(3, 1, 2);
        graph.addEdge(3, 2);
        graph.addEdge(3, 1);
        graph.addEdge(3, 2);
        graph.addVertex(3);
        assertEquals(List.of(1, 2), graph.getNeighbors(3));
        graph.removeEdge(3, 2);
        assertFalse(graph.hasEdge(3, 2));
        assertEquals(List.of(1), graph.getNeighbors(3));
    }

    @ParameterizedTest
    @EnumSource(GraphType.class)
    void oppositeEdgesCanBeRemovedSeparately(GraphType type) {
        Graph graph = type.create(1, 2, 3);
        graph.addEdge(1, 2);
        graph.addEdge(2, 1);
        graph.addEdge(2, 3);
        graph.removeEdge(1, 2);
        graph.removeEdge(1, 2);
        assertFalse(graph.hasEdge(1, 2));
        assertTrue(graph.hasEdge(2, 1));
        assertTrue(graph.hasEdge(2, 3));
        graph.removeEdge(2, 3);
        assertEquals(List.of(1), graph.getNeighbors(2));
        graph.removeEdge(2, 1);
        assertTrue(graph.getNeighbors(2).isEmpty());
        graph.addEdge(3, 1);
        assertEquals(List.of(1), graph.getNeighbors(3));
    }

    @ParameterizedTest
    @EnumSource(GraphType.class)
    void selfLoopsAreStoredOnce(GraphType type) {
        Graph graph = type.create(5, 6);
        graph.addEdge(5, 6);
        graph.addEdge(5, 5);
        graph.addEdge(5, 5);
        assertTrue(graph.hasEdge(5, 5));
        assertFalse(graph.hasEdge(6, 6));
        assertEquals(List.of(5, 6), graph.getNeighbors(5));
        graph.removeEdge(5, 5);
        assertFalse(graph.hasEdge(5, 5));
        assertEquals(List.of(6), graph.getNeighbors(5));
    }

    @ParameterizedTest
    @EnumSource(GraphType.class)
    void addingVertexPreservesExistingEdges(GraphType type) {
        Graph graph = type.create(10, 20);
        graph.addEdge(10, 20);
        graph.addEdge(20, 20);
        graph.addVertex(-1);
        assertTrue(graph.hasEdge(10, 20));
        assertTrue(graph.hasEdge(20, 20));
        assertTrue(graph.getNeighbors(-1).isEmpty());
        graph.addEdge(-1, 10);
        assertEquals(List.of(10), graph.getNeighbors(-1));
    }

    @ParameterizedTest
    @EnumSource(GraphType.class)
    void deletingMiddleVertexRemovesOnlyItsEdges(GraphType type) {
        Graph graph = type.create(10, 20, 30, 40);
        graph.addEdge(10, 20);
        graph.addEdge(20, 30);
        graph.addEdge(30, 20);
        graph.addEdge(20, 20);
        graph.addEdge(10, 30);
        graph.addEdge(40, 10);
        graph.addEdge(40, 40);
        graph.removeVertex(20);
        assertEquals(List.of(10, 30, 40), graph.getVertices());
        assertEquals(List.of(30), graph.getNeighbors(10));
        assertTrue(graph.getNeighbors(30).isEmpty());
        assertEquals(List.of(10, 40), graph.getNeighbors(40));
        graph.addEdge(30, 40);
        assertTrue(graph.hasEdge(30, 40));
        graph.addVertex(20);
        assertTrue(graph.getNeighbors(20).isEmpty());
        assertFalse(graph.hasEdge(10, 20));
        graph.addEdge(20, 40);
        assertEquals(List.of(40), graph.getNeighbors(20));
    }

    @ParameterizedTest
    @EnumSource(GraphType.class)
    void deletingFirstLastAndOnlyVertices(GraphType type) {
        Graph graph = type.create(3, 1, 2);
        graph.addEdge(1, 2);
        graph.removeVertex(3);
        assertTrue(graph.hasEdge(1, 2));
        graph.removeVertex(2);
        assertEquals(List.of(1), graph.getVertices());
        assertTrue(graph.getNeighbors(1).isEmpty());
        graph.addEdge(1, 1);
        graph.removeVertex(1);
        assertTrue(graph.getVertices().isEmpty());
        graph.addVertex(7);
        graph.addEdge(7, 7);
        assertEquals(List.of(7), graph.getNeighbors(7));
    }

    @ParameterizedTest
    @EnumSource(GraphType.class)
    void removingUnknownVertexDoesNothing(GraphType type) {
        Graph graph = type.create(1, 2);
        graph.addEdge(1, 2);
        graph.removeVertex(99);
        assertEquals(List.of(1, 2), graph.getVertices());
        assertTrue(graph.hasEdge(1, 2));
    }

    @ParameterizedTest
    @EnumSource(GraphType.class)
    void returnedListsDoNotExposeStorage(GraphType type) {
        Graph graph = type.create(1, 2);
        graph.addEdge(1, 2);
        List<Integer> vertices = graph.getVertices();
        List<Integer> neighbors = graph.getNeighbors(1);
        vertices.clear();
        neighbors.add(99);
        graph.addVertex(3);
        graph.removeEdge(1, 2);
        assertEquals(List.of(1, 2, 3), graph.getVertices());
        assertTrue(graph.getNeighbors(1).isEmpty());
        assertEquals(List.of(2, 99), neighbors);
        assertTrue(vertices.isEmpty());
    }

    @ParameterizedTest
    @EnumSource(GraphType.class)
    void unknownEndpointsThrowWithoutChangingGraph(GraphType type) {
        Graph graph = type.create(1, 2);
        graph.addEdge(1, 2);
        assertThrows(IllegalArgumentException.class, () -> graph.addEdge(99, 1));
        assertThrows(IllegalArgumentException.class, () -> graph.addEdge(1, 99));
        assertThrows(IllegalArgumentException.class, () -> graph.removeEdge(99, 1));
        assertThrows(IllegalArgumentException.class, () -> graph.removeEdge(1, 99));
        assertThrows(IllegalArgumentException.class, () -> graph.hasEdge(99, 1));
        assertThrows(IllegalArgumentException.class, () -> graph.hasEdge(1, 99));
        assertThrows(IllegalArgumentException.class, () -> graph.getNeighbors(99));
        assertEquals(List.of(1, 2), graph.getVertices());
        assertEquals(List.of(2), graph.getNeighbors(1));
    }

    @ParameterizedTest
    @EnumSource(GraphType.class)
    void clearAllowsReuse(GraphType type) {
        Graph graph = type.create(1, 2, 3);
        graph.addEdge(1, 2);
        graph.addEdge(2, 2);
        graph.clear();
        graph.clear();
        assertTrue(graph.getVertices().isEmpty());
        graph.addVertex(3);
        graph.addVertex(4);
        assertFalse(graph.hasEdge(3, 4));
        graph.addEdge(4, 3);
        assertEquals(List.of(3), graph.getNeighbors(4));
    }

    /** Сверяет последовательность изменений с независимой моделью из множеств. */
    @ParameterizedTest
    @EnumSource(GraphType.class)
    void randomOperationsMatchSets(GraphType type) {
        Graph graph = type.create();
        Set<Integer> vertices = new HashSet<>();
        Set<List<Integer>> edges = new HashSet<>();
        Random random = new Random(421);
        for (int step = 0; step < 500; step++) {
            int from = random.nextInt(9) - 4;
            int to = random.nextInt(9) - 4;
            switch (random.nextInt(4)) {
                case 0 -> {
                    graph.addVertex(from);
                    vertices.add(from);
                }
                case 1 -> {
                    graph.removeVertex(from);
                    vertices.remove(from);
                    edges.removeIf(edge -> edge.contains(from));
                }
                case 2 -> {
                    if (vertices.contains(from) && vertices.contains(to)) {
                        graph.addEdge(from, to);
                        edges.add(List.of(from, to));
                    }
                }
                case 3 -> {
                    if (vertices.contains(from) && vertices.contains(to)) {
                        graph.removeEdge(from, to);
                        edges.remove(List.of(from, to));
                    }
                }
                default -> throw new AssertionError();
            }
            assertEquals(vertices.stream().sorted().toList(), graph.getVertices());
            for (int vertex : vertices) {
                List<Integer> expected = new ArrayList<>();
                for (int neighbor : vertices) {
                    boolean present = edges.contains(List.of(vertex, neighbor));
                    assertEquals(present, graph.hasEdge(vertex, neighbor));
                    if (present) {
                        expected.add(neighbor);
                    }
                }
                expected.sort(Integer::compareTo);
                assertEquals(expected, graph.getNeighbors(vertex));
            }
        }
    }
}
