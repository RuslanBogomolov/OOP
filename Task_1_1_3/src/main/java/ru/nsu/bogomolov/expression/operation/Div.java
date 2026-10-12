package ru.nsu.bogomolov.expression.operation;

import ru.nsu.bogomolov.expression.Expression;
import ru.nsu.bogomolov.expression.Number;

/**
 * Частное двух выражений с целочисленным делением.
 */
public final class Div extends BinaryExpression {
    /**
     * Создает операцию с двумя операндами.
     *
     * @param left левый операнд
     * @param right правый операнд
     * @throws NullPointerException если один из операндов равен null
     */
    public Div(Expression left, Expression right) {
        super(left, right);
    }

    /**
     * Возвращает символ математической операции.
     *
     * @return символ операции
     */
    @Override
    protected char symbol() {
        return '/';
    }

    /**
     * Выполняет арифметическую операцию над значениями операндов.
     *
     * @param a значение левого операнда
     * @param b значение правого операнда
     * @return результат операции
     * @throws ArithmeticException если правый операнд равен нулю
     */
    @Override
    protected int calculate(int a, int b) {
        if (b == 0) {
            throw new ArithmeticException("Деление на ноль");
        }
        return a / b;
    }

    /**
     * Применяет правило производной частного: (u' * v - u * v') / (v * v).
     *
     * @param variable имя переменной дифференцирования
     * @return дерево производной
     */
    @Override
    public Expression derivative(String variable) {
        return new Div(new Sub(new Mul(left.derivative(variable), right),
                new Mul(left, right.derivative(variable))), new Mul(right, right));
    }

    /**
     * Упрощает выражение без изменения исходного дерева.
     *
     * @return результат упрощения
     * @throws ArithmeticException при недопустимой арифметической операции в подвыражении
     *     или переполнении показателя
     */
    @Override
    public Expression simplify() {
        Expression numerator = left.simplify();
        Expression denominator = right.simplify();
        if (numerator instanceof Number && denominator instanceof Number) {
            int value = calculate(((Number) numerator).getValue(), ((Number) denominator).getValue());
            return new Number(value);
        }
        return new Div(numerator, denominator);
    }
}
