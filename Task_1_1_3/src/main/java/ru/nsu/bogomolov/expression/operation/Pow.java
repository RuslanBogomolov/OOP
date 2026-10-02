package ru.nsu.bogomolov.expression.operation;

import java.util.Map;
import java.util.Objects;
import ru.nsu.bogomolov.expression.Expression;
import ru.nsu.bogomolov.expression.Number;

/**
 * Степень выражения с постоянным целым неотрицательным показателем.
 */
public final class Pow extends Expression {
    /** Основание степени. */
    final Expression base;
    /** Постоянный неотрицательный показатель степени. */
    final int exponent;

    /**
     * Создает степень с постоянным показателем.
     *
     * @param base основание степени
     * @param exponent неотрицательный показатель
     * @throws IllegalArgumentException если показатель отрицательный
     * @throws NullPointerException если основание равно null
     */
    public Pow(Expression base, int exponent) {
        this.base = Objects.requireNonNull(base);
        if (exponent < 0) {
            throw new IllegalArgumentException("Показатель степени должен быть >= 0");
        }
        this.exponent = exponent;
    }

    /**
     * Вычисляет значение выражения по заданным значениям переменных.
     *
     * @param variables соответствие имен переменных их значениям
     * @return значение выражения
     */
    @Override
    public int evaluate(Map<String, Integer> variables) {
        return power(base.evaluate(variables), exponent);
    }

    /**
     * Возводит целое число в степень методом последовательного возведения в квадрат.
     *
     * @param value основание степени
     * @param exponent неотрицательный показатель
     * @return значение степени с обычным для int переполнением
     */
    private static int power(int value, int exponent) {
        int result = 1;
        while (exponent > 0) {
            if (exponent % 2 == 1) {
                result *= value;
            }
            value *= value;
            exponent /= 2;
        }
        return result;
    }

    /**
     * Применяет правило производной степени с постоянным показателем.
     *
     * @param variable имя переменной дифференцирования
     * @return дерево производной
     */
    @Override
    public Expression derivative(String variable) {
        if (exponent == 0) {
            return new Number(0);
        }
        return new Mul(new Mul(new Number(exponent), new Pow(base, exponent - 1)),
                base.derivative(variable));
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
        Expression simplified = base.simplify();
        if (exponent == 0) {
            return new Number(1);
        }
        if (exponent == 1) {
            return simplified;
        }
        if (simplified instanceof Number) {
            return new Number(power(((Number) simplified).getValue(), exponent));
        }
        if (simplified instanceof Pow) {
            Pow inner = (Pow) simplified;
            return new Pow(inner.base, Math.multiplyExact(inner.exponent, exponent));
        }
        return new Pow(simplified, exponent);
    }

    /**
     * Возвращает запись выражения со скобками вокруг операций.
     *
     * @return строковое представление выражения
     */
    @Override
    public String toString() {
        return "(" + base + "^" + exponent + ")";
    }

    /**
     * Сравнивает содержимое выражений.
     *
     * @param object объект для сравнения
     * @return true, если выражения совпадают
     */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Pow)) {
            return false;
        }
        Pow other = (Pow) object;
        return base.equals(other.base) && exponent == other.exponent;
    }

    /**
     * Вычисляет хеш-код, согласованный со сравнением выражений.
     *
     * @return хеш-код выражения
     */
    @Override
    public int hashCode() {
        return Objects.hash(base, exponent);
    }
}
