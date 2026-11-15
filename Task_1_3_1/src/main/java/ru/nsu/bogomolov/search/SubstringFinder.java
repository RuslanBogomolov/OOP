package ru.nsu.bogomolov.search;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Поиск всех вхождений подстроки в файле UTF-8 алгоритмом Кнута — Морриса — Пратта. */
public class SubstringFinder {

    /** Создает объект для поиска подстрок в файлах. */
    public SubstringFinder() {
    }

    /**
     * Последовательно читает файл и находит в том числе пересекающиеся вхождения.
     * Индексы считаются с нуля по кодовым точкам Unicode, а не по байтам или char.
     * Пробелы, переводы строк и регистр учитываются; нормализация не выполняется.
     * Файл не загружается целиком: память занимает подстрока, буфер чтения и результат.
     *
     * @param fileName имя или путь к файлу UTF-8
     * @param substring непустая искомая подстрока
     * @return позиции начала вхождений в порядке возрастания
     * @throws NullPointerException если один из аргументов равен null
     * @throws IllegalArgumentException если подстрока пуста
     * @throws IOException если файл недоступен, произошла ошибка чтения
     *                     или данные не соответствуют UTF-8
     */
    public List<Long> find(String fileName, String substring) throws IOException {
        Objects.requireNonNull(fileName, "Имя файла не должно быть null");
        Objects.requireNonNull(substring, "Подстрока не должна быть null");
        if (substring.isEmpty()) {
            throw new IllegalArgumentException("Подстрока не должна быть пустой");
        }

        int[] pattern = substring.codePoints().toArray();
        int[] prefix = buildPrefix(pattern);
        List<Long> positions = new ArrayList<>();
        int matched = 0;
        long position = 0;

        try (BufferedReader reader = Files.newBufferedReader(
                Path.of(fileName), StandardCharsets.UTF_8)) {
            int symbol;
            while ((symbol = readCodePoint(reader)) != -1) {
                // При несовпадении сохраняем уже найденный подходящий префикс.
                while (matched > 0 && pattern[matched] != symbol) {
                    matched = prefix[matched - 1];
                }
                if (pattern[matched] == symbol) {
                    matched++;
                }
                if (matched == pattern.length) {
                    positions.add(position - pattern.length + 1);
                    // Следующее вхождение может пересекаться с найденным.
                    matched = prefix[matched - 1];
                }
                position++;
            }
        }
        return positions;
    }

    /**
     * Строит таблицу длин совпадающих собственных префиксов и суффиксов.
     *
     * @param pattern кодовые точки подстроки
     * @return префиксная функция для каждой позиции
     */
    private static int[] buildPrefix(int[] pattern) {
        int[] prefix = new int[pattern.length];
        for (int i = 1; i < pattern.length; i++) {
            int length = prefix[i - 1];
            while (length > 0 && pattern[i] != pattern[length]) {
                length = prefix[length - 1];
            }
            if (pattern[i] == pattern[length]) {
                length++;
            }
            prefix[i] = length;
        }
        return prefix;
    }

    /**
     * Читает кодовую точку, объединяя суррогатную пару в один символ.
     * Декодер UTF-8 проверяет корректность входных данных.
     *
     * @param reader источник символов UTF-8
     * @return кодовая точка или -1 в конце файла
     * @throws IOException при ошибке чтения или некорректном UTF-8
     */
    private static int readCodePoint(BufferedReader reader) throws IOException {
        int first = reader.read();
        if (first == -1) {
            return -1;
        }
        if (Character.isHighSurrogate((char) first)) {
            return Character.toCodePoint((char) first, (char) reader.read());
        }
        return first;
    }
}
