package ru.nsu.bogomolov.expression;

import java.util.HashMap;
import java.util.Map;

/**
 * Математическое выражение, которое можно вычислить, продифференцировать и упростить.
 */
public abstract class Expression {
    /**
     * Вычисляет значение выражения по заданным значениям переменных.
     *
     * @param variables соответствие имен переменных их значениям
     * @return значение выражения
     */
    public abstract int evaluate(Map<String, Integer> variables);

    /**
     * Строит производную по указанной переменной без изменения исходного выражения.
     *
     * @param variable имя переменной дифференцирования
     * @return дерево производной
     */
    public abstract Expression derivative(String variable);

    /**
     * Упрощает выражение без изменения исходного дерева.
     *
     * @return результат упрощения
     */
    public abstract Expression simplify();

    /**
     * Возвращает запись выражения со скобками вокруг операций.
     *
     * @return строковое представление выражения
     */
    @Override
    public abstract String toString();

    /**
     * Печатает выражение в стандартный поток вывода.
     */
    public final void print() {
        System.out.println(this);
    }

    /**
     * Разбирает означивания вида «x = 10; y = 13» и вычисляет выражение.
     *
     * @param assignments значения переменных, разделенные точкой с запятой
     * @return значение выражения
     * @throws IllegalArgumentException если означивание некорректно или переменная не задана
     * @throws ArithmeticException при делении на ноль
     */
    public final int eval(String assignments) {
        Map<String, Integer> variables = new HashMap<>();
        for (String assignment : assignments.split(";")) {
            if (assignment.trim().isEmpty()) {
                continue;
            }
            String[] parts = assignment.split("=", -1);
            if (parts.length != 2) {
                throw new IllegalArgumentException("Неверное означивание: " + assignment);
            }
            String name = parts[0].trim();
            if (!name.matches("[\\p{L}_][\\p{L}\\p{N}_]*")) {
                throw new IllegalArgumentException("Неверное имя переменной: " + name);
            }
            variables.put(name, Integer.parseInt(parts[1].trim()));
        }
        return evaluate(variables);
    }
}
