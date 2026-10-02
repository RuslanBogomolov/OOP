package ru.nsu.bogomolov.expression.operation;

import java.util.Map;
import java.util.Objects;

import ru.nsu.bogomolov.expression.Expression;

/**
 * Общая основа операций с левым и правым операндами.
 */
public abstract class BinaryExpression extends Expression {
    /** Левый операнд. */
    protected final Expression left;
    /** Правый операнд. */
    protected final Expression right;

    /**
     * Создает операцию с двумя операндами.
     *
     * @param left левый операнд
     * @param right правый операнд
     * @throws NullPointerException если один из операндов равен null
     */
    protected BinaryExpression(Expression left, Expression right) {
        this.left = Objects.requireNonNull(left);
        this.right = Objects.requireNonNull(right);
    }

    /**
     * Возвращает символ математической операции.
     *
     * @return символ операции
     */
    protected abstract char symbol();

    /**
     * Выполняет арифметическую операцию над значениями операндов.
     *
     * @param a значение левого операнда
     * @param b значение правого операнда
     * @return результат операции
     */
    protected abstract int calculate(int a, int b);

    /**
     * Вычисляет значение выражения по заданным значениям переменных.
     *
     * @param variables соответствие имен переменных их значениям
     * @return значение выражения
     */
    @Override
    public final int evaluate(Map<String, Integer> variables) {
        return calculate(left.evaluate(variables), right.evaluate(variables));
    }

    /**
     * Возвращает запись выражения со скобками вокруг операций.
     *
     * @return строковое представление выражения
     */
    @Override
    public final String toString() {
        return "(" + left + symbol() + right + ")";
    }

    /**
     * Сравнивает содержимое выражений.
     *
     * @param object объект для сравнения
     * @return true, если выражения совпадают
     */
    @Override
    public final boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        BinaryExpression other = (BinaryExpression) object;
        return left.equals(other.left) && right.equals(other.right);
    }

    /**
     * Вычисляет хеш-код, согласованный со сравнением выражений.
     *
     * @return хеш-код выражения
     */
    @Override
    public final int hashCode() {
        return Objects.hash(getClass(), left, right);
    }
}
