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
        Simplifier.Sum sum = new Simplifier.Sum();
        left.simplify().collectTerms(false, sum);
        right.simplify().collectTerms(true, sum);
        return sum.toExpression();
    }

    /**
     * Передает левое и правое слагаемые с противоположными знаками.
     *
     * @param negative true, если разность находится под отрицательным знаком
     * @param sum накопитель слагаемых
     */
    @Override
    public void collectTerms(boolean negative, Simplifier.Sum sum) {
        left.collectTerms(negative, sum);
        right.collectTerms(!negative, sum);
    }
}
