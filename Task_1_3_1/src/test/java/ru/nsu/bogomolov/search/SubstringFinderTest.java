package ru.nsu.bogomolov.search;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/** Проверки поиска в обычных, некорректных и больших файлах UTF-8. */
class SubstringFinderTest {

    @TempDir
    Path directory;

    private final SubstringFinder finder = new SubstringFinder();

    /** Проверяет позиции вхождений для разных текстов и подстрок. */
    @ParameterizedTest
    @MethodSource("examples")
    void findsOccurrences(String text, String pattern, List<Long> expected) throws IOException {
        Path file = writeText(text);
        assertEquals(expected, finder.find(file.toString(), pattern));
    }

    /** Возвращает примеры, включая пересечения, разные алфавиты и откаты КМП. */
    private static Stream<Arguments> examples() {
        return Stream.of(
                Arguments.of("абракадабра", "бра", List.of(1L, 8L)),
                Arguments.of("", "а", List.of()),
                Arguments.of("аб", "абв", List.of()),
                Arguments.of("привет", "мир", List.of()),
                Arguments.of("текст", "текст", List.of(0L)),
                Arguments.of("абв", "а", List.of(0L)),
                Arguments.of("абв", "в", List.of(2L)),
                Arguments.of("аааа", "а", List.of(0L, 1L, 2L, 3L)),
                Arguments.of("ааааа", "ааа", List.of(0L, 1L, 2L)),
                Arguments.of("abababa", "ababa", List.of(0L, 2L)),
                Arguments.of("aaaaabaaaaab", "aaab", List.of(2L, 8L)),
                Arguments.of("abababacababaca", "ababaca", List.of(2L, 8L)),
                Arguments.of("aabaaabaaac", "aabaaac", List.of(4L)),
                Arguments.of("Привет привет", "привет", List.of(7L)),
                Arguments.of("日本語 Ελληνικά 日本語", "日本語", List.of(0L, 13L)),
                Arguments.of("a\r\nb\nc", "\nb\n", List.of(2L)),
                Arguments.of("a\u0000b\u0000", "\u0000", List.of(1L, 3L)),
                Arguments.of("😀𐐀я😀𐐀", "😀𐐀", List.of(0L, 3L)),
                Arguments.of("😀абра😀бра", "бра", List.of(2L, 6L)),
                Arguments.of("😀😀😀", "😀😀", List.of(0L, 1L)),
                Arguments.of("é e\u0301", "é", List.of(0L))
        );
    }

    /** Проверяет подстроку на границе буферов чтения. */
    @Test
    void findsAcrossBufferBoundary() throws IOException {
        Path file = writeText("я".repeat(8191) + "начало-конец");
        assertEquals(List.of(8191L), finder.find(file.toString(), "начало-конец"));
    }

    /** Проверяет многобайтные символы и суррогатные пары возле границы буфера. */
    @Test
    void findsSupplementaryCharactersAcrossBufferBoundary() throws IOException {
        Path file = writeText("a".repeat(8191) + "😀𐐀я😀");
        assertEquals(List.of(8191L), finder.find(file.toString(), "😀𐐀я"));
        assertEquals(List.of(8193L), finder.find(file.toString(), "я😀"));
    }

    /** Проверяет отказ от поиска пустой строки. */
    @Test
    void rejectsEmptyPattern() {
        assertThrows(IllegalArgumentException.class,
                () -> finder.find(directory.resolve("input.txt").toString(), ""));
    }

    /** Проверяет оба обязательных аргумента. */
    @Test
    void rejectsNullArguments() {
        assertThrows(NullPointerException.class, () -> finder.find(null, "а"));
        assertThrows(NullPointerException.class, () -> finder.find("input.txt", null));
    }

    /** Проверяет передачу ошибки открытия файла вызывающему коду. */
    @Test
    void reportsMissingFile() {
        assertThrows(NoSuchFileException.class,
                () -> finder.find(directory.resolve("missing.txt").toString(), "а"));
    }

    /** Проверяет, что поврежденная последовательность UTF-8 не заменяется символом. */
    @Test
    void rejectsMalformedUtf8() throws IOException {
        Path file = directory.resolve("invalid.txt");
        Files.write(file, new byte[]{(byte) 0xD0});
        assertThrows(MalformedInputException.class, () -> finder.find(file.toString(), "а"));
    }

    /** Сравнивает КМП с прямым перебором на воспроизводимых случайных примерах. */
    @Test
    void matchesNaiveSearch() throws IOException {
        Random random = new Random(2026);
        int[] alphabet = {'а', 'б', 'я', '日', 0x1F600};
        for (int attempt = 0; attempt < 200; attempt++) {
            String text = randomText(random, alphabet, random.nextInt(150));
            String pattern = randomText(random, alphabet, 1 + random.nextInt(8));
            Path file = writeText(text);
            assertEquals(naiveSearch(text, pattern), finder.find(file.toString(), pattern),
                    () -> "Текст: " + text + "; подстрока: " + pattern);
        }
    }

    /** Генерирует файл 128 МиБ при ограничении кучи тестовой JVM в 64 МиБ. */
    @Test
    void searchesFileLargerThanHeap() throws IOException {
        Path file = directory.resolve("large.txt");
        String block = "a".repeat(8192);
        int blockCount = 16384;
        long middle = (long) block.length() * (blockCount / 2);
        try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            for (int i = 0; i < blockCount; i++) {
                if (i == blockCount / 2) {
                    writer.write("я😀я");
                }
                writer.write(block);
            }
            writer.write("я😀я");
        }
        long end = (long) block.length() * blockCount + 3;
        assertEquals(List.of(middle, end), finder.find(file.toString(), "я😀я"));
    }

    /** Записывает небольшой текст во временный файл UTF-8. */
    private Path writeText(String text) throws IOException {
        Path file = directory.resolve("input.txt");
        return Files.writeString(file, text, StandardCharsets.UTF_8);
    }

    /** Создает случайную строку из заданных кодовых точек. */
    private static String randomText(Random random, int[] alphabet, int length) {
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.appendCodePoint(alphabet[random.nextInt(alphabet.length)]);
        }
        return text.toString();
    }

    /** Независимо проверяет каждую возможную позицию подстроки в небольшом тексте. */
    private static List<Long> naiveSearch(String text, String pattern) {
        int[] symbols = text.codePoints().toArray();
        int[] target = pattern.codePoints().toArray();
        List<Long> result = new ArrayList<>();
        for (int i = 0; i <= symbols.length - target.length; i++) {
            int j = 0;
            while (j < target.length && symbols[i + j] == target[j]) {
                j++;
            }
            if (j == target.length) {
                result.add((long) i);
            }
        }
        return result;
    }
}
