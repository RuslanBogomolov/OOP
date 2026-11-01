package ru.nsu.bogomolov;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Проверяет запуск примера и сообщения об ошибках. */
class MainTest {
    @TempDir
    Path directory;

    @Test
    void exampleFile() throws IOException {
        Path path = write("4 4\n10 20 30 40\n10 20\n10 30\n20 40\n30 40\n");
        String[] output = capture(path.toString());
        assertTrue(output[0].contains("10 -> [20, 30]"));
        assertTrue(output[0].contains("Алгоритм Кана: ["));
        assertTrue(output[0].contains("Обход в глубину: ["));
        assertEquals("", output[1]);
    }

    @Test
    void customFile() throws IOException {
        Path path = write("3 2\n9 5 1\n9 5\n5 1\n");
        String[] output = capture(path.toString());
        assertTrue(output[0].contains("Алгоритм Кана: [9, 5, 1]"));
        assertTrue(output[0].contains("Обход в глубину: [9, 5, 1]"));
        assertEquals("", output[1]);
    }

    @Test
    void cycleIsExplained() throws IOException {
        String[] output = capture(write("2 2\n1 2\n1 2\n2 1\n").toString());
        assertTrue(output[0].contains("Алгоритм Кана: сортировка невозможна"));
        assertTrue(output[0].contains("Обход в глубину: сортировка невозможна"));
        assertEquals("", output[1]);
    }

    @Test
    void missingFileIsExplained() {
        String[] output = capture(directory.resolve("missing.txt").toString());
        assertEquals("", output[0]);
        assertTrue(output[1].startsWith("Не удалось прочитать граф: "));
    }

    @Test
    void invalidFileIsExplained() throws IOException {
        String[] output = capture(write("wrong format").toString());
        assertEquals("", output[0]);
        assertTrue(output[1].contains("Ожидалось целое число"));
    }

    private Path write(String content) throws IOException {
        Path path = directory.resolve("graph.txt");
        return Files.writeString(path, content);
    }

    private String[] capture(String... args) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ByteArrayOutputStream errors = new ByteArrayOutputStream();
        PrintStream oldOutput = System.out;
        PrintStream oldErrors = System.err;
        try (PrintStream out = new PrintStream(output, true, StandardCharsets.UTF_8);
                PrintStream err = new PrintStream(errors, true, StandardCharsets.UTF_8)) {
            System.setOut(out);
            System.setErr(err);
            Main.main(args);
        } finally {
            System.setOut(oldOutput);
            System.setErr(oldErrors);
        }
        return new String[]{output.toString(StandardCharsets.UTF_8),
                errors.toString(StandardCharsets.UTF_8)};
    }
}
