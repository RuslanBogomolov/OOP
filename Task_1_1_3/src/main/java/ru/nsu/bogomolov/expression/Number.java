package ru.nsu.bogomolov.expression;

import java.util.Map;

/**
 * Целочисленная константа.
 */
public final class Number extends Expression {
    /** Значение константы. */
    private final int value;

    /**
     * Создает целочисленную константу.
     *
     * @param value значение константы
     */
    public Number(int value) {
        this.value = value;
    }

    /**
     * Возвращает значение константы.
     *
     * @return хранимое целое число
     */
    public int getValue() {
        return value;
    }

    /**
     * Вычисляет значение выражения по заданным значениям переменных.
     *
     * @param variables соответствие имен переменных их значениям
     * @return значение выражения
     */
    @Override
    public int evaluate(Map<String, Integer> variables) {
        return value;
    }

    /**
     * Возвращает нулевую производную константы.
     *
     * @param variable имя переменной дифференцирования
     * @return дерево производной
     */
    @Override
    public Expression derivative(String variable) {
        return new Number(0);
    }

    /**
     * Упрощает выражение без изменения исходного дерева.
     *
     * @return результат упрощения
     */
    @Override
    public Expression simplify() {
        return new Number(value);
    }

    /**
     * Возвращает запись выражения со скобками вокруг операций.
     *
     * @return строковое представление выражения
     */
    @Override
    public String toString() {
        return Integer.toString(value);
    }

    /**
     * Сравнивает содержимое выражений.
     *
     * @param object объект для сравнения
     * @return true, если выражения совпадают
     */
    @Override
    public boolean equals(Object object) {
        return object instanceof Number && value == ((Number) object).value;
    }

    /**
     * Вычисляет хеш-код, согласованный со сравнением выражений.
     *
     * @return хеш-код выражения
     */
    @Override
    public int hashCode() {
        return Integer.hashCode(value);
    }
}
