package ru.nsu.bogomolov.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Представляет ориентированный граф матрицей смежности.
 * Строка соответствует началу ребра, а столбец — его концу.
 */
public class AdjacencyMatrixGraph extends AbstractGraph {
    private boolean[][] matrix = new boolean[0][0];
    private final List<Integer> vertices = new ArrayList<>();
    private final Map<Integer, Integer> indices = new HashMap<>();

    /** Создаёт пустой граф, хранящий рёбра матрицей смежности. */
    public AdjacencyMatrixGraph() {
    }

    @Override
    public void addVertex(int vertex) {
        if (indices.containsKey(vertex)) {
            return;
        }
        int size = vertices.size();
        boolean[][] expanded = new boolean[size + 1][size + 1];
        for (int row = 0; row < size; row++) {
            System.arraycopy(matrix[row], 0, expanded[row], 0, size);
        }
        matrix = expanded;
        indices.put(vertex, size);
        vertices.add(vertex);
    }

    @Override
    public void removeVertex(int vertex) {
        Integer removed = indices.get(vertex);
        if (removed == null) {
            return;
        }
        int size = vertices.size();
        boolean[][] reduced = new boolean[size - 1][size - 1];
        int newRow = 0;
        for (int row = 0; row < size; row++) {
            if (row == removed) {
                continue;
            }
            int newColumn = 0;
            for (int column = 0; column < size; column++) {
                if (column != removed) {
                    reduced[newRow][newColumn++] = matrix[row][column];
                }
            }
            newRow++;
        }
        matrix = reduced;
        vertices.remove((int) removed);
        indices.remove(vertex);
        for (int i = removed; i < vertices.size(); i++) {
            indices.put(vertices.get(i), i);
        }
    }

    @Override
    public void addEdge(int from, int to) {
        matrix[indexOf(from)][indexOf(to)] = true;
    }

    @Override
    public void removeEdge(int from, int to) {
        matrix[indexOf(from)][indexOf(to)] = false;
    }

    @Override
    public boolean hasEdge(int from, int to) {
        return matrix[indexOf(from)][indexOf(to)];
    }

    @Override
    public List<Integer> getVertices() {
        List<Integer> result = new ArrayList<>(vertices);
        Collections.sort(result);
        return result;
    }

    @Override
    public List<Integer> getNeighbors(int vertex) {
        int row = indexOf(vertex);
        List<Integer> neighbors = new ArrayList<>();
        for (int column = 0; column < vertices.size(); column++) {
            if (matrix[row][column]) {
                neighbors.add(vertices.get(column));
            }
        }
        Collections.sort(neighbors);
        return neighbors;
    }

    private int indexOf(int vertex) {
        Integer index = indices.get(vertex);
        if (index == null) {
            throw new IllegalArgumentException("Нет вершины: " + vertex);
        }
        return index;
    }
}
