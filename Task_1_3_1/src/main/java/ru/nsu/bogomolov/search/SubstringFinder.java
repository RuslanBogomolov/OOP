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
            char[] buffer = new char[8192];
            char highSurrogate = 0;
            int count;
            while ((count = reader.read(buffer)) != -1) {
                for (int i = 0; i < count; i++) {
                    int symbol = buffer[i];
                    if (highSurrogate != 0) {
                        symbol = Character.toCodePoint(highSurrogate, buffer[i]);
                        highSurrogate = 0;
                    } else if (Character.isHighSurrogate(buffer[i])) {
                        // Сохраняем первую половину пары, в том числе на границе буфера.
                        highSurrogate = buffer[i];
                        continue;
                    }
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

}
