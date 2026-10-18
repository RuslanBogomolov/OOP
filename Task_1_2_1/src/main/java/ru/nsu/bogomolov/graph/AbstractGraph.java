package ru.nsu.bogomolov.graph;

import java.util.List;
import java.util.StringJoiner;

/** Общие операции, не зависящие от хранения графа. */
public abstract class AbstractGraph implements Graph {

    /** Создаёт базовую реализацию графа. */
    public AbstractGraph() {
    }

    /** Сравнивает вершины и рёбра независимо от способа хранения. */
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Graph other)) {
            return false;
        }
        List<Integer> vertices = getVertices();
        if (!vertices.equals(other.getVertices())) {
            return false;
        }
        for (int vertex : vertices) {
            if (!getNeighbors(vertex).equals(other.getNeighbors(vertex))) {
                return false;
            }
        }
        return true;
    }

    /** Вычисляет хеш по вершинам и исходящим рёбрам. */
    @Override
    public int hashCode() {
        int result = 1;
        for (int vertex : getVertices()) {
            result = 31 * result + Integer.hashCode(vertex);
            result = 31 * result + getNeighbors(vertex).hashCode();
        }
        return result;
    }

    /** Записывает каждую вершину и её исходящих соседей в отдельную строку. */
    @Override
    public String toString() {
        StringJoiner result = new StringJoiner("\n");
        for (int vertex : getVertices()) {
            result.add(vertex + " -> " + getNeighbors(vertex));
        }
        return result.toString();
    }
}
