package ru.nsu.bogomolov.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import ru.nsu.bogomolov.graph.Graph;
import ru.nsu.bogomolov.support.GraphType;

/** Проверяет чтение файлов и сохранность графа при ошибках. */
class GraphFileReaderTest {
    @TempDir
    Path directory;

    @ParameterizedTest
    @EnumSource(GraphType.class)
    void readsVerticesEdgesLoopsAndIsolatedVertices(GraphType type) throws IOException {
        Path path = write("4 3\n20 -5 0 8\n20 -5\n-5 0\n0 0\n");
        Graph graph = type.create();
        graph.readFromFile(path);
        assertEquals(List.of(-5, 0, 8, 20), graph.getVertices());
        assertEquals(List.of(-5), graph.getNeighbors(20));
        assertEquals(List.of(0), graph.getNeighbors(-5));
        assertEquals(List.of(0), graph.getNeighbors(0));
        assertTrue(graph.getNeighbors(8).isEmpty());
    }

    @ParameterizedTest
    @EnumSource(GraphType.class)
    void replacesRatherThanAppendsAndCanReadAgain(GraphType type) throws IOException {
        Graph graph = type.create(100, 200);
        graph.addEdge(100, 200);
        graph.readFromFile(write("2 1\n1 2\n2 1\n"));
        assertEquals(List.of(1, 2), graph.getVertices());
        assertEquals(List.of(1), graph.getNeighbors(2));
        graph.readFromFile(write("1 0\n7\n"));
        assertEquals(List.of(7), graph.getVertices());
        assertTrue(graph.getNeighbors(7).isEmpty());
    }

    @ParameterizedTest
    @EnumSource(GraphType.class)
    void emptyGraphClearsOldContent(GraphType type) throws IOException {
        Graph graph = type.create(1, 2);
        graph.addEdge(1, 2);
        graph.readFromFile(write("0 0\n"));
        assertTrue(graph.getVertices().isEmpty());
    }

    @ParameterizedTest
    @EnumSource(GraphType.class)
    void acceptsWhitespaceAndExtremeNumbers(GraphType type) throws IOException {
        Graph graph = type.create();
        graph.readFromFile(write("""
                \s
                2\t1\r
                -2147483648 2147483647
                -2147483648\t2147483647
                
                """));
        assertEquals(List.of(Integer.MIN_VALUE, Integer.MAX_VALUE), graph.getVertices());
        assertTrue(graph.hasEdge(Integer.MIN_VALUE, Integer.MAX_VALUE));
    }

    static Stream<Arguments> invalidFiles() {
        List<String> contents = List.of(
                "", "   ", "2", "-1 0", "0 -1", "-1 -1",
                "two 0", "1 0 x", "2147483648 0", "1 0 -2147483649",
                "2 0 1", "2 0 1 1", "2 1 1 2", "2 1 1 2 1",
                "2 1 1 2 9 1", "2 1 1 2 1 9",
                "2 2 1 2 1 2 1 2", "1 2 7 7 7 7 7",
                "0 0 extra", "1 0 1 5", "0 1 1 1", "0.0 0");
        return Stream.of(GraphType.values()).flatMap(type ->
                contents.stream().map(content -> Arguments.of(type, content)));
    }

    @ParameterizedTest
    @MethodSource("invalidFiles")
    void invalidFileLeavesExistingGraphUnchanged(GraphType type, String content)
            throws IOException {
        Graph graph = type.create(10, 20);
        graph.addEdge(10, 20);
        graph.addEdge(20, 20);
        String before = graph.toString();
        int hash = graph.hashCode();
        Path path = write(content);
        assertThrows(IOException.class, () -> graph.readFromFile(path));
        assertEquals(before, graph.toString());
        assertEquals(hash, graph.hashCode());
    }

    @ParameterizedTest
    @EnumSource(GraphType.class)
    void missingFileLeavesGraphUnchanged(GraphType type) {
        Graph graph = type.create(1);
        graph.addEdge(1, 1);
        assertThrows(IOException.class,
                () -> graph.readFromFile(directory.resolve("missing.txt")));
        assertEquals(List.of(1), graph.getVertices());
        assertTrue(graph.hasEdge(1, 1));
    }

    @ParameterizedTest
    @EnumSource(GraphType.class)
    void malformedUtf8IsRejected(GraphType type) throws IOException {
        Path path = directory.resolve("broken.txt");
        Files.write(path, new byte[]{(byte) 0xc3, (byte) 0x28});
        Graph graph = type.create(1);
        assertThrows(IOException.class, () -> graph.readFromFile(path));
        assertEquals(List.of(1), graph.getVertices());
    }

    private Path write(String content) throws IOException {
        Path path = directory.resolve("graph.txt");
        Files.writeString(path, content);
        return path;
    }
}
