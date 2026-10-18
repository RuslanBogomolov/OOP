package ru.nsu.bogomolov;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import ru.nsu.bogomolov.graph.AdjacencyListGraph;
import ru.nsu.bogomolov.graph.Graph;
import ru.nsu.bogomolov.sort.DepthFirstSort;
import ru.nsu.bogomolov.sort.KahnSort;
import ru.nsu.bogomolov.sort.TopologicalSort;

/** Пример чтения графа и запуска двух сортировок. */
public class Main {

    /** Создаёт объект, запускающий пример работы с графом. */
    public Main() {
    }

    /**
     * Запускает пример. Без аргументов читает examples/graph.txt.
     *
     * @param args необязательный путь к файлу графа
     */
    public static void main(String[] args) {
        Path path = args.length == 0
                ? defaultExamplePath()
                : Path.of(args[0]);
        Graph graph = new AdjacencyListGraph();
        new Main().run(path, graph);
    }

    private static Path defaultExamplePath() {
        Path projectPath = Path.of("examples/cycle.txt");
        if (Files.isRegularFile(projectPath)) {
            return projectPath;
        }

        Path repositoryPath = Path.of("Task_1_2_1", "examples", "graph.txt");
        if (Files.isRegularFile(repositoryPath)) {
            return repositoryPath;
        }

        return projectPath;
    }

    /**
     * Читает файл и выводит граф вместе с результатами сортировок.
     *
     * @param path путь к файлу
     * @param graph выбранное представление графа
     */
    public void run(Path path, Graph graph) {
        try {
            graph.readFromFile(path);
            System.out.println("Граф:");
            System.out.println(graph);
            printOrder("Алгоритм Кана", new KahnSort(), graph);
            printOrder("Обход в глубину", new DepthFirstSort(), graph);
        } catch (IOException exception) {
            System.err.println("Не удалось прочитать граф: " + exception.getMessage());
        }
    }

    private void printOrder(String name, TopologicalSort sort, Graph graph) {
        try {
            System.out.println(name + ": " + sort.sort(graph));
        } catch (IllegalStateException exception) {
            System.out.println(name + ": сортировка невозможна — в графе есть цикл");
        }
    }
}
