package ru.nsu.bogomolov.sort;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import ru.nsu.bogomolov.graph.Graph;

/** Последовательно выбирает вершины с нулевой входящей степенью. */
public class KahnSort implements TopologicalSort {

    @Override
    public List<Integer> sort(Graph graph) {
        List<Integer> vertices = graph.getVertices();
        Map<Integer, Integer> degrees = new HashMap<>();
        for (int vertex : vertices) {
            degrees.put(vertex, 0);
        }
        for (int vertex : vertices) {
            for (int neighbor : graph.getNeighbors(vertex)) {
                degrees.put(neighbor, degrees.get(neighbor) + 1);
            }
        }
        Queue<Integer> ready = new ArrayDeque<>();
        for (int vertex : vertices) {
            if (degrees.get(vertex) == 0) {
                ready.add(vertex);
            }
        }
        List<Integer> result = new ArrayList<>();
        while (!ready.isEmpty()) {
            int vertex = ready.remove();
            result.add(vertex);
            for (int neighbor : graph.getNeighbors(vertex)) {
                int degree = degrees.get(neighbor) - 1;
                degrees.put(neighbor, degree);
                if (degree == 0) {
                    ready.add(neighbor);
                }
            }
        }
        if (result.size() != vertices.size()) {
            throw new IllegalStateException("В графе есть цикл");
        }
        return result;
    }
}
