package ru.nsu.bogomolov.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import ru.nsu.bogomolov.graph.Graph;

/** Читает количество вершин и рёбер, номера вершин, затем пары концов рёбер. */
public class GraphFileReader {

    /** Создаёт объект для чтения графов из файлов. */
    public GraphFileReader() {
    }

    /**
     * Проверяет файл целиком и заменяет содержимое графа.
     *
     * @param path путь к файлу в UTF-8
     * @param graph граф для заполнения
     * @throws IOException если файл недоступен или содержит неверные данные
     */
    public void read(Path path, Graph graph) throws IOException {
        Map<Integer, Set<Integer>> adjacency = new LinkedHashMap<>();
        try (Scanner scanner = new Scanner(Files.readString(path))) {
            int vertexCount = readInt(scanner);
            int edgeCount = readInt(scanner);
            if (vertexCount < 0 || edgeCount < 0) {
                throw new IOException("Количество вершин и рёбер не может быть отрицательным");
            }
            for (int i = 0; i < vertexCount; i++) {
                int vertex = readInt(scanner);
                if (adjacency.putIfAbsent(vertex, new LinkedHashSet<>()) != null) {
                    throw new IOException("Повтор вершины: " + vertex);
                }
            }
            for (int i = 0; i < edgeCount; i++) {
                int from = readInt(scanner);
                int to = readInt(scanner);
                if (!adjacency.containsKey(from) || !adjacency.containsKey(to)) {
                    throw new IOException("Ребро содержит неизвестную вершину");
                }
                if (!adjacency.get(from).add(to)) {
                    throw new IOException("Повтор ребра: " + from + " -> " + to);
                }
            }
            if (scanner.hasNext()) {
                throw new IOException("Лишние данные в конце файла");
            }
        }
        graph.clear();
        for (int vertex : adjacency.keySet()) {
            graph.addVertex(vertex);
        }
        for (Map.Entry<Integer, Set<Integer>> entry : adjacency.entrySet()) {
            for (int neighbor : entry.getValue()) {
                graph.addEdge(entry.getKey(), neighbor);
            }
        }
    }

    private int readInt(Scanner scanner) throws IOException {
        if (!scanner.hasNext()) {
            throw new IOException("Неожиданный конец файла");
        }
        String token = scanner.next();
        try {
            return Integer.parseInt(token);
        } catch (NumberFormatException exception) {
            throw new IOException("Ожидалось целое число: " + token, exception);
        }
    }
}
