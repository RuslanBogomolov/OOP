package ru.nsu.bogomolov;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import ru.nsu.bogomolov.expression.Expression;
import ru.nsu.bogomolov.expression.Number;
import ru.nsu.bogomolov.expression.Variable;
import ru.nsu.bogomolov.expression.operation.Div;
import ru.nsu.bogomolov.parser.ExpressionParser;

/** Проверяет операцию деления. */
class DivTest {

    /**
     * Проверяет вычисление операции над двумя константами.
     */
    @Test
    void evaluatesTwoOperands() {
        assertEquals(3, new Div(new Number(10), new Number(3)).eval(""));
    }

    /**
     * Проверяет рекурсивное вычисление переменного операнда.
     */
    @Test
    void evaluatesVariableOperand() {
        assertEquals(3, new Div(new Variable("x"), new Number(3)).eval("x=10"));
    }

    /**
     * Проверяет печать операции со скобками.
     */
    @Test
    void printsParenthesizedExpression() {
        assertEquals("(x/3)", new Div(new Variable("x"), new Number(3)).toString());
    }

    /**
     * Проверяет сворачивание операции над константами.
     */
    @Test
    void simplifiesConstants() {
        assertEquals(new Number(3), new Div(new Number(10), new Number(3)).simplify());
    }

    /**
     * Проверяет правило производной частного.
     */
    @Test
    void buildsQuotientDerivative() {
        Expression expression = ExpressionParser.parse("x/x");
        assertEquals("(((1*x)-(x*1))/(x*x))", expression.derivative("x").toString());
        assertEquals(0, expression.derivative("x").eval("x=4"));
    }

    /**
     * Проверяет усечение результата целочисленного деления.
     */
    @Test
    void usesIntegerDivision() {
        assertEquals(2, ExpressionParser.parse("5/2").eval(""));
        assertEquals(-2, ExpressionParser.parse("-5/2").eval(""));
    }

    /**
     * Проверяет деление на ноль при вычислении.
     */
    @Test
    void rejectsZeroDenominatorDuringEvaluation() {
        assertThrows(ArithmeticException.class, () -> ExpressionParser.parse("x/0").eval("x=2"));
    }

    /**
     * Проверяет деление на ноль при сворачивании констант.
     */
    @Test
    void rejectsZeroDenominatorDuringSimplification() {
        assertThrows(ArithmeticException.class, () -> ExpressionParser.parse("1/0").simplify());
    }

    /**
     * Проверяет сохранение дроби с переменным числителем.
     */
    @Test
    void keepsVariableNumerator() {
        assertEquals("(x/2)", ExpressionParser.parse("x/2").simplify().toString());
    }

    /**
     * Проверяет сохранение дроби с переменным знаменателем.
     */
    @Test
    void keepsVariableDenominator() {
        assertEquals("(2/x)", ExpressionParser.parse("2/x").simplify().toString());
    }

    /**
     * Проверяет сохранение проверки нулевого знаменателя после упрощения.
     */
    @Test
    void doesNotCancelPotentialZeroDenominator() {
        Expression simplified = ExpressionParser.parse("x/x").simplify();
        assertEquals("(x/x)", simplified.toString());
        assertThrows(ArithmeticException.class, () -> simplified.eval("x=0"));
    }
}