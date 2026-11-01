package ru.nsu.bogomolov.support;

import ru.nsu.bogomolov.graph.AdjacencyListGraph;
import ru.nsu.bogomolov.graph.AdjacencyMatrixGraph;
import ru.nsu.bogomolov.graph.Graph;
import ru.nsu.bogomolov.graph.IncidenceMatrixGraph;

/** Создаёт разные представления графа для одинаковых проверок. */
public enum GraphType {
    ADJACENCY_LIST,
    ADJACENCY_MATRIX,
    INCIDENCE_MATRIX;

    /** Создаёт граф с указанными вершинами. */
    public Graph create(int... vertices) {
        Graph graph = switch (this) {
            case ADJACENCY_LIST -> new AdjacencyListGraph();
            case ADJACENCY_MATRIX -> new AdjacencyMatrixGraph();
            case INCIDENCE_MATRIX -> new IncidenceMatrixGraph();
        };
        for (int vertex : vertices) {
            graph.addVertex(vertex);
        }
        return graph;
    }
}
