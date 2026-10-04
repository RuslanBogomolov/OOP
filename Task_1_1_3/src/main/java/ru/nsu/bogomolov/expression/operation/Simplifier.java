package ru.nsu.bogomolov.expression.operation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import ru.nsu.bogomolov.expression.Expression;
import ru.nsu.bogomolov.expression.Number;

/**
 * Объединяет одинаковые слагаемые и множители при упрощении выражений.
 */
public final class Simplifier {
    /** Накопитель числового коэффициента и множителей произведения. */
    public static final class Product {
        /** Общий числовой коэффициент произведения. */
        private int coefficient = 1;
        /** Основания множителей и количество их повторений. */
        private final Map<Expression, Integer> factors = new LinkedHashMap<>();

        /**
         * Умножает общий коэффициент на указанное значение.
         *
         * @param value множитель коэффициента
         */
        public void multiplyCoefficient(int value) {
            coefficient *= value;
        }

        /**
         * Добавляет множитель с заданной степенью.
         *
         * @param expression добавляемый множитель
         * @param exponent показатель степени множителя
         */
        public void addFactor(Expression expression, int exponent) {
            factors.merge(expression, exponent, Math::addExact);
        }

        /**
         * Возвращает накопленный коэффициент.
         *
         * @return числовой коэффициент произведения
         */
        public int coefficient() {
            return coefficient;
        }

        /**
         * Проверяет, содержит ли произведение только числовой коэффициент.
         *
         * @return {@code true}, если множители отсутствуют
         */
        public boolean isConstant() {
            return factors.isEmpty();
        }

        /**
         * Строит выражение из множителей без числового коэффициента.
         *
         * @return выражение из множителей либо единица
         */
        public Expression factorsExpression() {
            List<Expression> bases = new ArrayList<>(factors.keySet());
            // Единый порядок позволяет сравнить x*y и y*x.
            bases.sort(Comparator.comparing(Expression::toString));
            Expression result = null;
            for (Expression base : bases) {
                int exponent = factors.get(base);
                Expression factor = exponent == 1 ? base : new Pow(base, exponent);
                result = result == null ? factor : new Mul(result, factor);
            }
            return result == null ? new Number(1) : result;
        }

        /**
         * Строит произведение с учетом накопленного коэффициента.
         *
         * @return упрощенное произведение
         */
        public Expression toExpression() {
            if (coefficient == 0) {
                return new Number(0);
            }
            if (factors.isEmpty()) {
                return new Number(coefficient);
            }
            Expression body = factorsExpression();
            return coefficient == 1 ? body : new Mul(new Number(coefficient), body);
        }
    }

    /** Накопитель коэффициентов одинаковых слагаемых. */
    public static final class Sum {
        private final Map<Expression, Integer> terms = new LinkedHashMap<>();

        /**
         * Добавляет свободный числовой коэффициент.
         *
         * @param value добавляемая константа
         */
        public void addConstant(int value) {
            terms.merge(null, value, Math::addExact);
        }

        /**
         * Добавляет слагаемое с указанным коэффициентом.
         *
         * @param expression часть слагаемого без коэффициента
         * @param coefficient коэффициент слагаемого
         */
        public void addTerm(Expression expression, int coefficient) {
            terms.merge(expression, coefficient, Math::addExact);
        }

        /**
         * Строит сумму из накопленных слагаемых.
         *
         * @return упрощенная сумма
         */
        public Expression toExpression() {
            Expression result = null;
            for (Map.Entry<Expression, Integer> term : terms.entrySet()) {
                if (term.getValue() == 0) {
                    continue;
                }
                Expression value;
                if (term.getKey() == null) {
                    value = new Number(term.getValue());
                } else {
                    Product product = new Product();
                    product.multiplyCoefficient(term.getValue());
                    product.addFactor(term.getKey(), 1);
                    value = product.toExpression();
                }
                result = result == null ? value : new Add(result, value);
            }
            return result == null ? new Number(0) : result;
        }
    }
}
