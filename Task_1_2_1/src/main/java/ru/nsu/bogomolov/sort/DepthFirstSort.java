package ru.nsu.bogomolov.sort;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ru.nsu.bogomolov.graph.Graph;

/** Сортирует обходом в глубину с проверкой циклов по цветам вершин. */
public class DepthFirstSort implements TopologicalSort {

    @Override
    public List<Integer> sort(Graph graph) {
        Map<Integer, Integer> colors = new HashMap<>();
        List<Integer> result = new ArrayList<>();
        for (int vertex : graph.getVertices()) {
            visit(vertex, graph, colors, result);
        }
        Collections.reverse(result);
        return result;
    }

    /** Цвет 1 означает текущий путь обхода, цвет 2 — завершённую вершину. */
    private void visit(int vertex, Graph graph, Map<Integer, Integer> colors,
                       List<Integer> result) {
        int color = colors.getOrDefault(vertex, 0);
        if (color == 1) {
            throw new IllegalStateException("В графе есть цикл");
        }
        if (color == 2) {
            return;
        }
        colors.put(vertex, 1);
        for (int neighbor : graph.getNeighbors(vertex)) {
            visit(neighbor, graph, colors, result);
        }
        colors.put(vertex, 2);
        result.add(vertex);
    }
}
