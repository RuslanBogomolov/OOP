package ru.nsu.bogomolov.graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Матрица инцидентности: строки — вершины, столбцы — рёбра.
 * Начало ребра отмечено -1, конец — 1, петля — 2.
 */
public class IncidenceMatrixGraph extends AbstractGraph {
    private int[][] matrix = new int[0][0];
    private final List<Integer> vertices = new ArrayList<>();
    private final Map<Integer, Integer> indices = new HashMap<>();
    private int edgeCount;

    @Override
    public void addVertex(int vertex) {
        if (indices.containsKey(vertex)) {
            return;
        }
        int size = vertices.size();
        matrix = Arrays.copyOf(matrix, size + 1);
        matrix[size] = new int[edgeCount];
        indices.put(vertex, size);
        vertices.add(vertex);
    }

    @Override
    public void removeVertex(int vertex) {
        Integer removed = indices.get(vertex);
        if (removed == null) {
            return;
        }
        List<Integer> remainingEdges = new ArrayList<>();
        for (int edge = 0; edge < edgeCount; edge++) {
            if (matrix[removed][edge] == 0) {
                remainingEdges.add(edge);
            }
        }
        int[][] reduced = new int[vertices.size() - 1][remainingEdges.size()];
        int newRow = 0;
        for (int row = 0; row < vertices.size(); row++) {
            if (row == removed) {
                continue;
            }
            for (int column = 0; column < remainingEdges.size(); column++) {
                reduced[newRow][column] = matrix[row][remainingEdges.get(column)];
            }
            newRow++;
        }
        matrix = reduced;
        edgeCount = remainingEdges.size();
        vertices.remove((int) removed);
        indices.remove(vertex);
        for (int i = removed; i < vertices.size(); i++) {
            indices.put(vertices.get(i), i);
        }
    }

    @Override
    public void addEdge(int from, int to) {
        int start = indexOf(from);
        int end = indexOf(to);
        if (findEdge(start, end) != -1) {
            return;
        }
        for (int row = 0; row < vertices.size(); row++) {
            matrix[row] = Arrays.copyOf(matrix[row], edgeCount + 1);
        }
        if (start == end) {
            matrix[start][edgeCount] = 2;
        } else {
            matrix[start][edgeCount] = -1;
            matrix[end][edgeCount] = 1;
        }
        edgeCount++;
    }

    @Override
    public void removeEdge(int from, int to) {
        int edge = findEdge(indexOf(from), indexOf(to));
        if (edge == -1) {
            return;
        }
        for (int row = 0; row < vertices.size(); row++) {
            int[] reduced = new int[edgeCount - 1];
            System.arraycopy(matrix[row], 0, reduced, 0, edge);
            System.arraycopy(matrix[row], edge + 1, reduced, edge, edgeCount - edge - 1);
            matrix[row] = reduced;
        }
        edgeCount--;
    }

    @Override
    public boolean hasEdge(int from, int to) {
        return findEdge(indexOf(from), indexOf(to)) != -1;
    }

    @Override
    public List<Integer> getVertices() {
        List<Integer> result = new ArrayList<>(vertices);
        Collections.sort(result);
        return result;
    }

    @Override
    public List<Integer> getNeighbors(int vertex) {
        int start = indexOf(vertex);
        List<Integer> neighbors = new ArrayList<>();
        for (int edge = 0; edge < edgeCount; edge++) {
            if (matrix[start][edge] == 2) {
                neighbors.add(vertex);
            } else if (matrix[start][edge] == -1) {
                int end = 0;
                while (matrix[end][edge] != 1) {
                    end++;
                }
                neighbors.add(vertices.get(end));
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

    private int findEdge(int start, int end) {
        for (int edge = 0; edge < edgeCount; edge++) {
            if ((start == end && matrix[start][edge] == 2)
                    || (matrix[start][edge] == -1 && matrix[end][edge] == 1)) {
                return edge;
            }
        }
        return -1;
    }
}
