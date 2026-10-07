package ru.nsu.bogomolov.expression;

/**
 * Проверяет допустимость имен переменных.
 */
public final class VariableNames {
    /**
     * Запрещает создание объектов вспомогательного класса.
     */
    private VariableNames() {
    }

    /**
     * Проверяет имя переменной.
     *
     * @param name проверяемое имя
     * @throws IllegalArgumentException если имя отсутствует или имеет неверный формат
     */
    public static void validate(String name) {
        if (name == null || !name.matches("[\\p{L}_][\\p{L}\\p{N}_]*")) {
            throw new IllegalArgumentException("Неверное имя переменной: " + name);
        }
    }
}
