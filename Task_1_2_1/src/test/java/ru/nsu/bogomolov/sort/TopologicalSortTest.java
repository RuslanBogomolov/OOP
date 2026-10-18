package ru.nsu.bogomolov.sort;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.nsu.bogomolov.graph.Graph;
import ru.nsu.bogomolov.support.GraphType;

/** Проверяет оба алгоритма на каждом представлении графа. */
class TopologicalSortTest {

    static Stream<Arguments> implementations() {
        return Stream.of(GraphType.values()).flatMap(type -> Stream.of(
                Arguments.of(type, new KahnSort()),
                Arguments.of(type, new DepthFirstSort())));
    }

    @ParameterizedTest
    @MethodSource("implementations")
    void emptyGraph(GraphType type, TopologicalSort sort) {
        assertTrue(sort.sort(type.create()).isEmpty());
    }

    @ParameterizedTest
    @MethodSource("implementations")
    void singleVertex(GraphType type, TopologicalSort sort) {
        assertEquals(List.of(42), sort.sort(type.create(42)));
    }

    @ParameterizedTest
    @MethodSource("implementations")
    void chainHasOnlyOneOrder(GraphType type, TopologicalSort sort) {
        Graph graph = type.create(0, 10, -5, 8);
        graph.addEdge(10, -5);
        graph.addEdge(-5, 8);
        graph.addEdge(8, 0);
        assertEquals(List.of(10, -5, 8, 0), sort.sort(graph));
    }

    @ParameterizedTest
    @MethodSource("implementations")
    void branchingDagWithSharedNeighborAndIsolatedVertex(GraphType type, TopologicalSort sort) {
        Graph graph = type.create(1, 2, 3, 4, 5, 6, 7);
        graph.addEdge(1, 2);
        graph.addEdge(1, 3);
        graph.addEdge(2, 4);
        graph.addEdge(3, 4);
        graph.addEdge(4, 5);
        graph.addEdge(3, 5);
        graph.addEdge(6, 3);
        String before = graph.toString();
        assertOrder(graph, sort.sort(graph));
        assertEquals(before, graph.toString());
    }

    @ParameterizedTest
    @MethodSource("implementations")
    void graphWithoutEdges(GraphType type, TopologicalSort sort) {
        Graph graph = type.create(8, -1, 3);
        assertOrder(graph, sort.sort(graph));
    }

    @ParameterizedTest
    @MethodSource("implementations")
    void disconnectedComponents(GraphType type, TopologicalSort sort) {
        Graph graph = type.create(1, 2, 3, 4, 5, 6);
        graph.addEdge(1, 2);
        graph.addEdge(3, 4);
        graph.addEdge(4, 5);
        assertOrder(graph, sort.sort(graph));
    }

    @ParameterizedTest
    @MethodSource("implementations")
    void rejectsSelfLoop(GraphType type, TopologicalSort sort) {
        Graph graph = type.create(1);
        graph.addEdge(1, 1);
        assertThrows(IllegalStateException.class, () -> sort.sort(graph));
        assertTrue(graph.hasEdge(1, 1));
    }

    @ParameterizedTest
    @MethodSource("implementations")
    void rejectsTwoVertexCycle(GraphType type, TopologicalSort sort) {
        Graph graph = type.create(1, 2);
        graph.addEdge(1, 2);
        graph.addEdge(2, 1);
        assertThrows(IllegalStateException.class, () -> sort.sort(graph));
    }

    @ParameterizedTest
    @MethodSource("implementations")
    void rejectsCycleInAnotherComponent(GraphType type, TopologicalSort sort) {
        Graph graph = type.create(1, 2, 3, 4, 5);
        graph.addEdge(1, 2);
        graph.addEdge(3, 4);
        graph.addEdge(4, 5);
        graph.addEdge(5, 3);
        String before = graph.toString();
        assertThrows(IllegalStateException.class, () -> sort.sort(graph));
        assertEquals(before, graph.toString());
    }

    @ParameterizedTest
    @MethodSource("implementations")
    void detectsCycleReachableFromSource(GraphType type, TopologicalSort sort) {
        Graph graph = type.create(1, 2, 3, 4);
        graph.addEdge(1, 2);
        graph.addEdge(2, 3);
        graph.addEdge(3, 2);
        graph.addEdge(3, 4);
        assertThrows(IllegalStateException.class, () -> sort.sort(graph));
    }

    @ParameterizedTest
    @MethodSource("implementations")
    void algorithmCanBeReusedAfterSuccessAndFailure(GraphType type, TopologicalSort sort) {
        Graph graph = type.create(1, 2);
        graph.addEdge(1, 2);
        assertEquals(List.of(1, 2), sort.sort(graph));
        graph.addEdge(2, 1);
        assertThrows(IllegalStateException.class, () -> sort.sort(graph));
        graph.removeEdge(1, 2);
        assertEquals(List.of(2, 1), sort.sort(graph));
        assertTrue(sort.sort(type.create()).isEmpty());
    }

    @ParameterizedTest
    @MethodSource("implementations")
    void randomAcyclicGraphsHaveValidOrders(GraphType type, TopologicalSort sort) {
        Random random = new Random(121);
        for (int attempt = 0; attempt < 20; attempt++) {
            Graph graph = type.create();
            for (int vertex = 0; vertex < 12; vertex++) {
                graph.addVertex(vertex - 6);
            }
            for (int from = -6; from < 6; from++) {
                for (int to = from + 1; to < 6; to++) {
                    if (random.nextBoolean()) {
                        graph.addEdge(from, to);
                    }
                }
            }
            assertOrder(graph, sort.sort(graph));
        }
    }

    /** Проверяет все рёбра, так как правильных порядков может быть несколько. */
    private void assertOrder(Graph graph, List<Integer> order) {
        assertEquals(graph.getVertices().size(), order.size());
        assertEquals(new HashSet<>(graph.getVertices()), new HashSet<>(order));
        Map<Integer, Integer> positions = new HashMap<>();
        for (int i = 0; i < order.size(); i++) {
            positions.put(order.get(i), i);
        }
        for (int from : graph.getVertices()) {
            for (int to : graph.getNeighbors(from)) {
                assertTrue(positions.get(from) < positions.get(to),
                        () -> "Нарушен порядок ребра " + from + " -> " + to);
            }
        }
    }
}
