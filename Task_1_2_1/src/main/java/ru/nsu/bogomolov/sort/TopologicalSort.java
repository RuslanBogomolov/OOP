package ru.nsu.bogomolov.sort;

import java.util.List;
import ru.nsu.bogomolov.graph.Graph;

/** Алгоритм топологической сортировки ориентированного графа. */
public interface TopologicalSort {

    /**
     * Возвращает вершины так, что начало каждого ребра идёт раньше его конца.
     * Исходный граф не изменяется.
     *
     * @param graph исходный граф
     * @return топологический порядок вершин
     * @throws IllegalStateException если в графе есть цикл
     */
    List<Integer> sort(Graph graph);
}
