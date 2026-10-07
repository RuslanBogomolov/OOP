package ru.nsu.bogomolov;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import ru.nsu.bogomolov.expression.Expression;
import ru.nsu.bogomolov.expression.Number;
import ru.nsu.bogomolov.expression.Variable;
import ru.nsu.bogomolov.expression.operation.Add;
import ru.nsu.bogomolov.parser.ExpressionParser;

/** Проверяет базовую реализацию бинарных выражений. */
class BinaryExpressionTest {

    /**
     * Проверяет отказ от отсутствующего левого операнда.
     */
    @Test
    void rejectsNullLeftOperand() {
        assertThrows(NullPointerException.class, () -> new Add(null, new Number(1)));
    }

    /**
     * Проверяет отказ от отсутствующего правого операнда.
     */
    @Test
    void rejectsNullRightOperand() {
        assertThrows(NullPointerException.class, () -> new Add(new Number(1), null));
    }

    /**
     * Проверяет структурное равенство деревьев и их хеш-кодов.
     */
    @Test
    void comparesEqualTrees() {
        Expression first = ExpressionParser.parse("x+2");
        Expression second = ExpressionParser.parse("x+2");
        assertEquals(first, first);
        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    /**
     * Проверяет различие операций над одинаковыми операндами.
     */
    @Test
    void distinguishesOperationTypes() {
        assertNotEquals(ExpressionParser.parse("x+2"), ExpressionParser.parse("x-2"));
    }

    /**
     * Проверяет различие деревьев с разными левыми операндами.
     */
    @Test
    void distinguishesLeftOperands() {
        assertNotEquals(ExpressionParser.parse("x+2"), ExpressionParser.parse("y+2"));
    }

    /**
     * Проверяет различие деревьев с разными правыми операндами.
     */
    @Test
    void distinguishesRightOperands() {
        assertNotEquals(ExpressionParser.parse("x+2"), ExpressionParser.parse("x+3"));
    }

    /**
     * Проверяет сравнение бинарной операции с null.
     */
    @Test
    void doesNotEqualNull() {
        assertFalse(ExpressionParser.parse("x+2").equals(null));
    }

    /**
     * Проверяет сравнение операции с простым выражением.
     */
    @Test
    void doesNotEqualLeaf() {
        assertNotEquals(ExpressionParser.parse("x+2"), new Variable("x"));
    }
}