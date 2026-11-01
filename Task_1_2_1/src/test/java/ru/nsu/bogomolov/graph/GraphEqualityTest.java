package ru.nsu.bogomolov.graph;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import ru.nsu.bogomolov.support.GraphType;

/** Проверяет равенство, хеширование и строковое представление. */
class GraphEqualityTest {

    static Stream<Arguments> pairs() {
        return Stream.of(GraphType.values()).flatMap(left ->
                Stream.of(GraphType.values()).map(right -> Arguments.of(left, right)));
    }

    @ParameterizedTest
    @MethodSource("pairs")
    void sameStructureIsEqualRegardlessOfStorageAndInsertionOrder(
            GraphType left, GraphType right) {
        Graph first = left.create(3, 1, 2, 10);
        first.addEdge(1, 3);
        first.addEdge(1, 2);
        first.addEdge(3, 3);
        Graph second = right.create(10, 2, 1, 3);
        second.addEdge(3, 3);
        second.addEdge(1, 2);
        second.addEdge(1, 3);
        assertEquals(first, second);
        assertEquals(second, first);
        assertEquals(first.hashCode(), second.hashCode());
        assertEquals(first.toString(), second.toString());
        Set<Graph> graphs = new HashSet<>();
        graphs.add(first);
        graphs.add(second);
        assertEquals(1, graphs.size());
    }

    @ParameterizedTest
    @MethodSource("pairs")
    void emptyGraphsAreEqual(GraphType left, GraphType right) {
        Graph first = left.create();
        Graph second = right.create();
        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @ParameterizedTest
    @MethodSource("pairs")
    void differentVerticesEdgesAndDirectionsAreNotEqual(GraphType left, GraphType right) {
        Graph first = left.create(1, 2);
        Graph second = right.create(1, 3);
        assertNotEquals(first, second);
        second.removeVertex(3);
        second.addVertex(2);
        first.addEdge(1, 2);
        assertNotEquals(first, second);
        second.addEdge(2, 1);
        assertNotEquals(first, second);
        second.removeEdge(2, 1);
        second.addEdge(1, 2);
        second.addVertex(99);
        assertNotEquals(first, second);
        second.removeVertex(99);
        second.addEdge(2, 2);
        assertNotEquals(first, second);
    }

    @ParameterizedTest
    @EnumSource(GraphType.class)
    void equalityIsReflexiveAndRejectsOtherTypes(GraphType type) {
        Graph graph = type.create(1);
        assertEquals(graph, graph);
        assertNotEquals(null, graph);
        assertNotEquals("1 -> []", graph);
    }

    @ParameterizedTest
    @EnumSource(GraphType.class)
    void equalityIsTransitive(GraphType type) {
        Graph first = type.create(2, 1);
        Graph second = new AdjacencyListGraph();
        Graph third = new IncidenceMatrixGraph();
        for (Graph graph : new Graph[]{first, second, third}) {
            graph.addVertex(1);
            graph.addVertex(2);
            graph.addEdge(2, 1);
        }
        assertEquals(first, second);
        assertEquals(second, third);
        assertEquals(first, third);
    }

    @ParameterizedTest
    @EnumSource(GraphType.class)
    void stringListsSortedVerticesAndOutgoingNeighbors(GraphType type) {
        Graph graph = type.create(10, -1, 0);
        graph.addEdge(10, 10);
        graph.addEdge(10, -1);
        graph.addEdge(-1, 0);
        assertEquals("-1 -> [0]\n0 -> []\n10 -> [-1, 10]", graph.toString());
    }
}
