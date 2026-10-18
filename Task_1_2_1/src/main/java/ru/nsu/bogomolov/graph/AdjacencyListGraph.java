package ru.nsu.bogomolov.graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Представляет ориентированный граф списком смежности.
 * Для каждой вершины хранится множество её исходящих соседей.
 */
public class AdjacencyListGraph extends AbstractGraph {
    private final Map<Integer, Set<Integer>> adjacency = new TreeMap<>();

    @Override
    public void addVertex(int vertex) {
        adjacency.putIfAbsent(vertex, new TreeSet<>());
    }

    @Override
    public void removeVertex(int vertex) {
        adjacency.remove(vertex);
        for (Set<Integer> neighbors : adjacency.values()) {
            neighbors.remove(vertex);
        }
    }

    @Override
    public void addEdge(int from, int to) {
        checkVertex(from);
        checkVertex(to);
        adjacency.get(from).add(to);
    }

    @Override
    public void removeEdge(int from, int to) {
        checkVertex(from);
        checkVertex(to);
        adjacency.get(from).remove(to);
    }

    @Override
    public boolean hasEdge(int from, int to) {
        checkVertex(from);
        checkVertex(to);
        return adjacency.get(from).contains(to);
    }

    @Override
    public List<Integer> getVertices() {
        return new ArrayList<>(adjacency.keySet());
    }

    @Override
    public List<Integer> getNeighbors(int vertex) {
        checkVertex(vertex);
        return new ArrayList<>(adjacency.get(vertex));
    }

    private void checkVertex(int vertex) {
        if (!adjacency.containsKey(vertex)) {
            throw new IllegalArgumentException("Нет вершины: " + vertex);
        }
    }
}
