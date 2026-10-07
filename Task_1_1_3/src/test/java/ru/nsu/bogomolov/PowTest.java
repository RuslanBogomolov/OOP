package ru.nsu.bogomolov;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import ru.nsu.bogomolov.expression.Expression;
import ru.nsu.bogomolov.expression.Number;
import ru.nsu.bogomolov.expression.Variable;
import ru.nsu.bogomolov.expression.operation.Pow;
import ru.nsu.bogomolov.parser.ExpressionParser;

/** Проверяет операцию возведения в степень. */
class PowTest {

    /**
     * Проверяет вычисление четной степени.
     */
    @Test
    void evaluatesEvenExponent() {
        assertEquals(16, new Pow(new Number(2), 4).eval(""));
    }

    /**
     * Проверяет вычисление нечетной степени отрицательного числа.
     */
    @Test
    void evaluatesOddExponent() {
        assertEquals(-8, new Pow(new Number(-2), 3).eval(""));
    }

    /**
     * Проверяет вычисление нулевой степени.
     */
    @Test
    void evaluatesZeroExponent() {
        assertEquals(1, new Pow(new Number(4), 0).eval(""));
    }

    /**
     * Проверяет вычисление степени переменного основания.
     */
    @Test
    void evaluatesVariableBase() {
        assertEquals(27, new Pow(new Variable("x"), 3).eval("x=3"));
    }

    /**
     * Проверяет отказ от отрицательного показателя.
     */
    @Test
    void rejectsNegativeExponent() {
        assertThrows(IllegalArgumentException.class, () -> new Pow(new Number(2), -1));
    }

    /**
     * Проверяет отказ от отсутствующего основания.
     */
    @Test
    void rejectsNullBase() {
        assertThrows(NullPointerException.class, () -> new Pow(null, 2));
    }

    /**
     * Проверяет строковую запись степени.
     */
    @Test
    void printsPower() {
        assertEquals("(x^3)", new Pow(new Variable("x"), 3).toString());
    }

    /**
     * Проверяет нулевую производную степени с показателем 0.
     */
    @Test
    void differentiatesZeroExponent() {
        assertEquals(new Number(0), new Pow(new Variable("x"), 0).derivative("x"));
    }

    /**
     * Проверяет формулу производной степени.
     */
    @Test
    void differentiatesPositiveExponent() {
        Expression derivative = new Pow(new Variable("x"), 3).derivative("x");
        assertEquals("((3*(x^2))*1)", derivative.toString());
        assertEquals(12, derivative.eval("x=2"));
    }

    /**
     * Проверяет цепное правило для степени составного основания.
     */
    @Test
    void differentiatesCompoundBase() {
        assertEquals(12, ExpressionParser.parse("(2*x+1)^2").derivative("x").eval("x=1"));
    }

    /**
     * Проверяет замену нулевой степени на 1.
     */
    @Test
    void simplifiesZeroExponent() {
        assertEquals(new Number(1), new Pow(new Variable("x"), 0).simplify());
    }

    /**
     * Проверяет замену первой степени на основание.
     */
    @Test
    void simplifiesFirstExponent() {
        assertEquals(new Variable("x"), new Pow(new Variable("x"), 1).simplify());
    }

    /**
     * Проверяет вычисление степени постоянного основания.
     */
    @Test
    void simplifiesConstantBase() {
        assertEquals(new Number(8), new Pow(new Number(2), 3).simplify());
    }

    /**
     * Проверяет перемножение показателей вложенных степеней.
     */
    @Test
    void simplifiesNestedPower() {
        assertEquals(new Pow(new Variable("x"), 6), ExpressionParser.parse("(x^2)^3").simplify());
    }

    /**
     * Проверяет сохранение степени переменного основания.
     */
    @Test
    void keepsVariablePower() {
        assertEquals(new Pow(new Variable("x"), 3), new Pow(new Variable("x"), 3).simplify());
    }

    /**
     * Проверяет переполнение показателя вложенной степени.
     */
    @Test
    void rejectsExponentOverflowDuringSimplification() {
        assertThrows(ArithmeticException.class,
                () -> new Pow(new Pow(new Variable("x"), Integer.MAX_VALUE), 2).simplify());
    }

    /**
     * Проверяет равенство степеней и их хеш-кодов.
     */
    @Test
    void comparesEqualPowers() {
        Pow first = new Pow(new Variable("x"), 3);
        Pow second = new Pow(new Variable("x"), 3);
        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    /**
     * Проверяет различие степеней с разными основаниями.
     */
    @Test
    void distinguishesBases() {
        assertNotEquals(new Pow(new Variable("x"), 3), new Pow(new Variable("y"), 3));
    }

    /**
     * Проверяет различие степеней с разными показателями.
     */
    @Test
    void distinguishesExponents() {
        assertNotEquals(new Pow(new Variable("x"), 3), new Pow(new Variable("x"), 4));
    }

    /**
     * Проверяет сравнение степени с выражением другого типа.
     */
    @Test
    void doesNotEqualAnotherType() {
        assertNotEquals(new Pow(new Variable("x"), 3), new Variable("x"));
    }

    /**
     * Проверяет сравнение степени с null.
     */
    @Test
    void doesNotEqualNull() {
        assertFalse(new Pow(new Variable("x"), 3).equals(null));
    }
}