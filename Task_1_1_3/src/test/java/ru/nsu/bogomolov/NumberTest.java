package ru.nsu.bogomolov;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

import java.util.Map;
import org.junit.jupiter.api.Test;
import ru.nsu.bogomolov.expression.Expression;
import ru.nsu.bogomolov.expression.Number;
import ru.nsu.bogomolov.expression.Variable;
import ru.nsu.bogomolov.expression.operation.Add;

/** Проверяет числовые выражения. */
class NumberTest {

    /**
     * Проверяет получение значения константы.
     */
    @Test
    void returnsStoredValue() {
        assertEquals(-7, new Number(-7).getValue());
    }

    /**
     * Проверяет вычисление константы без означиваний.
     */
    @Test
    void evaluatesWithoutVariables() {
        assertEquals(12, new Number(12).evaluate(Map.of()));
    }

    /**
     * Проверяет нулевую производную константы.
     */
    @Test
    void hasZeroDerivative() {
        assertEquals(new Number(0), new Number(12).derivative("x"));
    }

    @Test
    void differentiatesConstantExpressionToZero() {
        assertEquals(new Number(0),
                new Add(new Number(3), new Number(5)).derivative("x").simplify());
    }

    /**
     * Проверяет сохранение значения при создании упрощенной константы.
     */
    @Test
    void simplifiesToEqualNewNumber() {
        Number original = new Number(12);
        Expression simplified = original.simplify();
        assertEquals(original, simplified);
        assertNotSame(original, simplified);
    }

    /**
     * Проверяет строковую запись отрицательной константы.
     */
    @Test
    void printsSignedValue() {
        assertEquals("-12", new Number(-12).toString());
    }

    /**
     * Проверяет равенство констант и согласованность хеш-кодов.
     */
    @Test
    void comparesEqualValues() {
        Number first = new Number(12);
        Number second = new Number(12);
        assertEquals(first, first);
        assertEquals(first, second);
        assertEquals(second, first);
        assertEquals(first.hashCode(), second.hashCode());
    }

    /**
     * Проверяет различие констант с разными значениями.
     */
    @Test
    void distinguishesDifferentValues() {
        assertNotEquals(new Number(1), new Number(2));
    }

    /**
     * Проверяет сравнение константы с другим типом.
     */
    @Test
    void doesNotEqualAnotherType() {
        assertNotEquals(new Number(1), new Variable("x"));
    }

    /**
     * Проверяет сравнение константы с null.
     */
    @Test
    void doesNotEqualNull() {
        assertFalse(new Number(1).equals(null));
    }
}