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
final class Simplifier {
    /**
     * Объединяет числовые коэффициенты и повторяющиеся множители.
     *
     * @param left упрощенный левый операнд
     * @param right упрощенный правый операнд
     * @return упрощенное произведение
     */
    static Expression product(Expression left, Expression right) {
        Product product = new Product();
        product.collect(left);
        product.collect(right);
        return product.toExpression();
    }

    /**
     * Собирает одинаковые слагаемые и складывает их коэффициенты.
     *
     * @param left упрощенный левый операнд
     * @param right упрощенный правый операнд
     * @param subtract true для вычитания правого операнда
     * @return упрощенная сумма или разность
     */
    static Expression sum(Expression left, Expression right, boolean subtract) {
        Map<Expression, Integer> terms = new LinkedHashMap<>();
        collectTerms(left, 1, terms);
        collectTerms(right, subtract ? -1 : 1, terms);
        Expression result = null;
        for (Map.Entry<Expression, Integer> term : terms.entrySet()) {
            int coefficient = term.getValue();
            if (coefficient == 0) {
                continue;
            }
            Expression value = product(new Number(coefficient), term.getKey());
            result = result == null ? value : new Add(result, value);
        }
        return result == null ? new Number(0) : result;
    }

    /**
     * Разбирает сумму на слагаемые и добавляет их коэффициенты в таблицу.
     *
     * @param expression обрабатываемое выражение
     * @param sign знак слагаемых: 1 или -1
     * @param terms таблица частей выражений и их коэффициентов
     */
    private static void collectTerms(Expression expression, int sign,
            Map<Expression, Integer> terms) {
        if (expression instanceof Add) {
            Add add = (Add) expression;
            collectTerms(add.left, sign, terms);
            collectTerms(add.right, sign, terms);
        } else {
            Product term = new Product();
            term.collect(expression);
            Expression factors = term.factorsExpression();
            terms.merge(factors, sign * term.coefficient, Integer::sum);
        }
    }

    /** Числовой коэффициент и таблица повторяющихся множителей. */
    private static final class Product {
        /** Общий числовой коэффициент произведения. */
        private int coefficient = 1;
        /** Основания множителей и количество их повторений. */
        private final Map<Expression, Integer> factors = new LinkedHashMap<>();

        /**
         * Добавляет число, произведение или степень к собираемым множителям.
         *
         * @param expression добавляемое выражение
         */
        private void collect(Expression expression) {
            if (expression instanceof Number) {
                coefficient *= ((Number) expression).getValue();
            } else if (expression instanceof Mul) {
                Mul mul = (Mul) expression;
                collect(mul.left);
                collect(mul.right);
            } else if (expression instanceof Pow) {
                Pow pow = (Pow) expression;
                factors.merge(pow.base, pow.exponent, Math::addExact);
            } else {
                factors.merge(expression, 1, Math::addExact);
            }
        }

        /**
         * Строит произведение множителей в едином порядке без числового коэффициента.
         *
         * @return произведение множителей либо константа 1
         */
        private Expression factorsExpression() {
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
         * Строит выражение из собранного коэффициента и множителей.
         *
         * @return произведение либо константа
         */
        private Expression toExpression() {
            if (coefficient == 0) {
                return new Number(0);
            }
            Expression body = factorsExpression();
            if (body instanceof Number) {
                return new Number(coefficient);
            }
            return coefficient == 1 ? body : new Mul(new Number(coefficient), body);
        }
    }
}
