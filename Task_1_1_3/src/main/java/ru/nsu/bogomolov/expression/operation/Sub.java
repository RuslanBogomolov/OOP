package ru.nsu.bogomolov.expression.operation;

import ru.nsu.bogomolov.expression.Expression;

/**
 * Разность двух выражений.
 */
public final class Sub extends BinaryExpression {
    /**
     * Создает операцию с двумя операндами.
     *
     * @param left левый операнд
     * @param right правый операнд
     * @throws NullPointerException если один из операндов равен null
     */
    public Sub(Expression left, Expression right) {
        super(left, right);
    }

    /**
     * Возвращает символ математической операции.
     *
     * @return символ операции
     */
    @Override
    protected char symbol() {
        return '-';
    }

    /**
     * Выполняет арифметическую операцию над значениями операндов.
     *
     * @param a значение левого операнда
     * @param b значение правого операнда
     * @return результат операции
     */
    @Override
    protected int calculate(int a, int b) {
        return a - b;
    }

    /**
     * Применяет правило производной разности: u' - v'.
     *
     * @param variable имя переменной дифференцирования
     * @return дерево производной
     */
    @Override
    public Expression derivative(String variable) {
        return new Sub(left.derivative(variable), right.derivative(variable));
    }

    /**
     * Упрощает выражение без изменения исходного дерева.
     *
     * @return результат упрощения
     */
    @Override
    public Expression simplify() {
        return Simplifier.sum(left.simplify(), right.simplify(), true);
    }
}
