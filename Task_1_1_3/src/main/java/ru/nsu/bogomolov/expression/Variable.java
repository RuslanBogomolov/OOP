package ru.nsu.bogomolov.expression;

import java.util.Map;

/**
 * Переменная с именем, значение которой задается при вычислении.
 */
public final class Variable extends Expression {
    /** Полное имя переменной с учетом регистра. */
    private final String name;

    /**
     * Создает переменную с указанным именем.
     *
     * @param name имя переменной
     * @throws IllegalArgumentException если имя отсутствует или имеет неверный формат
     */
    public Variable(String name) {
        VariableNames.validate(name);
        this.name = name;
    }

    /**
     * Вычисляет значение выражения по заданным значениям переменных.
     *
     * @param variables соответствие имен переменных их значениям
     * @return значение выражения
     * @throws IllegalArgumentException если значение переменной не задано
     */
    @Override
    public int evaluate(Map<String, Integer> variables) {
        Integer value = variables.get(name);
        if (value == null) {
            throw new IllegalArgumentException("Не задана переменная: " + name);
        }
        return value;
    }

    /**
     * Возвращает 1 для указанной переменной и 0 для остальных переменных.
     *
     * @param variable имя переменной дифференцирования
     * @return дерево производной
     */
    @Override
    public Expression derivative(String variable) {
        return new Number(name.equals(variable) ? 1 : 0);
    }

    /**
     * Упрощает выражение без изменения исходного дерева.
     *
     * @return результат упрощения
     */
    @Override
    public Expression simplify() {
        return new Variable(name);
    }

    /**
     * Возвращает запись выражения со скобками вокруг операций.
     *
     * @return строковое представление выражения
     */
    @Override
    public String toString() {
        return name;
    }

    /**
     * Сравнивает содержимое выражений.
     *
     * @param object объект для сравнения
     * @return true, если выражения совпадают
     */
    @Override
    public boolean equals(Object object) {
        return object instanceof Variable && name.equals(((Variable) object).name);
    }

    /**
     * Вычисляет хеш-код, согласованный со сравнением выражений.
     *
     * @return хеш-код выражения
     */
    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
