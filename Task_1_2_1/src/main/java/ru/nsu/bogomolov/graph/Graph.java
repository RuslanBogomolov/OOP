package ru.nsu.bogomolov.graph;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import ru.nsu.bogomolov.io.GraphFileReader;

/**
 * Ориентированный граф без кратных рёбер. Петли разрешены.
 * Вершины обозначаются целыми числами.
 */
public interface Graph {

    /**
     * Добавляет вершину. Повторное добавление ничего не меняет.
     *
     * @param vertex номер вершины
     */
    void addVertex(int vertex);

    /**
     * Удаляет вершину вместе с её рёбрами. Отсутствующая вершина игнорируется.
     *
     * @param vertex номер вершины
     */
    void removeVertex(int vertex);

    /**
     * Добавляет ребро from → to. Повторное добавление ничего не меняет.
     *
     * @param from начало ребра
     * @param to конец ребра
     * @throws IllegalArgumentException если хотя бы одной вершины нет
     */
    void addEdge(int from, int to);

    /**
     * Удаляет ребро from → to. Отсутствующее ребро игнорируется.
     *
     * @param from начало ребра
     * @param to конец ребра
     * @throws IllegalArgumentException если хотя бы одной вершины нет
     */
    void removeEdge(int from, int to);

    /**
     * Проверяет наличие ребра from → to.
     *
     * @param from начало ребра
     * @param to конец ребра
     * @return есть ли такое ребро
     * @throws IllegalArgumentException если хотя бы одной вершины нет
     */
    boolean hasEdge(int from, int to);

    /**
     * Возвращает отдельный список вершин по возрастанию.
     *
     * @return упорядоченные номера вершин
     */
    List<Integer> getVertices();

    /**
     * Возвращает отдельный список исходящих соседей по возрастанию.
     *
     * @param vertex номер вершины
     * @return упорядоченные номера соседей
     * @throws IllegalArgumentException если вершины нет
     */
    List<Integer> getNeighbors(int vertex);

    /** Удаляет все вершины и рёбра. */
    default void clear() {
        for (int vertex : getVertices()) {
            removeVertex(vertex);
        }
    }

    /**
     * Заменяет граф содержимым файла. При ошибке чтения граф сохраняется.
     *
     * @param path путь к файлу в UTF-8
     * @throws IOException если файл недоступен или имеет неверный формат
     */
    default void readFromFile(Path path) throws IOException {
        new GraphFileReader().read(path, this);
    }
}
