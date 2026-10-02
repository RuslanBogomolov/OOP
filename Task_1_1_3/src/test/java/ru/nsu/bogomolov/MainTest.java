package ru.nsu.bogomolov;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Проверяет консольное приложение. */
class MainTest {
    /** Временная папка для входных и выходных файлов теста. */
    @TempDir
    Path directory;

    /**
     * Проверяет полный запуск программы с вводом из консоли.
     *
     * @throws IOException если операция ввода или вывода завершилась ошибкой
     */
    @Test
    void evaluatesConsoleExpression() throws IOException {
        String output = runMain("3+2*x\nx\nx=10\n");
        assertTrue(output.contains("Выражение: (3+(2*x))"));
        assertTrue(output.contains("Упрощенная производная: 2"));
        assertTrue(output.contains("Значение: 23"));
    }

    /**
     * Проверяет повторный запрос после пустого или неверного выражения.
     *
     * @throws IOException если операция ввода или вывода завершилась ошибкой
     */
    @Test
    void retriesInvalidExpression() throws IOException {
        String output = runMain("\n3+2*x\nx\nx=10\n");
        assertTrue(output.contains("Неверное выражение:"));
        assertTrue(output.contains("Пример правильной записи: 3 + 2*x"));
        assertTrue(output.contains("Значение: 23"));
    }

    /**
     * Проверяет повторный запрос после пустого означивания.
     *
     * @throws IOException если операция ввода или вывода завершилась ошибкой
     */
    @Test
    void retriesEmptyAssignment() throws IOException {
        String output = runMain("X*X\nX\n\nX=2\n");
        assertTrue(output.contains("Не задана переменная: X. Повторите ввод."));
        assertTrue(output.contains("Значение: 4"));
    }

    /**
     * Проверяет повторный запрос при неверном регистре имени.
     *
     * @throws IOException если операция ввода или вывода завершилась ошибкой
     */
    @Test
    void retriesWrongVariableCase() throws IOException {
        String output = runMain("X\nX\nx=2\nX=2\n");
        assertTrue(output.contains("Не задана переменная: X"));
        assertTrue(output.contains("Значение: 2"));
    }

    /**
     * Проверяет повторный запрос после неправильной записи означивания.
     *
     * @throws IOException если операция ввода или вывода завершилась ошибкой
     */
    @Test
    void retriesMalformedAssignment() throws IOException {
        String output = runMain("x\nx\nx 2\nx=2\n");
        assertTrue(output.contains("Неверное означивание: x 2"));
        assertTrue(output.contains("Значение: 2"));
    }

    /**
     * Проверяет повторный запрос после деления на ноль при вычислении.
     *
     * @throws IOException если операция ввода или вывода завершилась ошибкой
     */
    @Test
    void retriesZeroDenominator() throws IOException {
        String output = runMain("1/x\nx\nx=0\nx=2\n");
        assertTrue(output.contains("Повторите ввод."));
        assertTrue(output.contains("Значение: 0"));
    }

    /**
     * Проверяет завершение при окончании входного потока.
     *
     * @throws IOException если операция ввода или вывода завершилась ошибкой
     */
    @Test
    void stopsAtEndOfInput() throws IOException {
        String output = runMain("x\nx\n");
        assertFalse(output.contains("Значение:"));
    }

    /**
     * Проверяет чтение выражения из файла и запись его строковой формы.
     *
     * @throws IOException если тестовые файлы не удалось прочитать или записать
     */
    @Test
    void readsAndWritesExpressionFiles() throws IOException {
        Path input = directory.resolve("input.txt");
        Path output = directory.resolve("output.txt");
        Files.writeString(input, "3 + 2*x", StandardCharsets.UTF_8);
        String console = runMain("x\nx=10\n", input.toString(), output.toString());
        assertTrue(console.contains("Значение: 23"));
        assertEquals("(3+(2*x))", Files.readString(output, StandardCharsets.UTF_8));
    }

    /**
     * Проверяет чтение входного файла без указания выходного файла.
     *
     * @throws IOException если тестовый файл не удалось прочитать или записать
     */
    @Test
    void readsFileWithoutOutputPath() throws IOException {
        Path input = directory.resolve("input.txt");
        Files.writeString(input, "4", StandardCharsets.UTF_8);
        assertTrue(runMain("x\n\n", input.toString()).contains("Значение: 4"));
    }

    /** Проверяет сообщение об отсутствующем входном файле. */
    @Test
    void reportsMissingInputFile() {
        Path input = directory.resolve("missing.txt");
        assertThrows(IOException.class, () -> runMain("", input.toString()));
    }

    /**
     * Запускает программу с заданным вводом и перехватывает текст вывода.
     * После запуска восстанавливает стандартные потоки.
     *
     * @param input строки, которые программа прочитает из консоли
     * @param args аргументы командной строки
     * @return текст, записанный программой в стандартный вывод
     * @throws IOException если программа не смогла прочитать или записать файл
     */
    private String runMain(String input, String... args) throws IOException {
        InputStream originalInput = System.in;
        PrintStream originalOutput = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (PrintStream capturedOutput = new PrintStream(output, true, StandardCharsets.UTF_8)) {
            System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
            System.setOut(capturedOutput);
            Main.main(args);
            return output.toString(StandardCharsets.UTF_8);
        } finally {
            System.setIn(originalInput);
            System.setOut(originalOutput);
        }
    }
}