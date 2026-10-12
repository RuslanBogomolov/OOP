package ru.nsu.bogomolov;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import ru.nsu.bogomolov.expression.Expression;
import ru.nsu.bogomolov.expression.Number;
import ru.nsu.bogomolov.expression.Variable;
import ru.nsu.bogomolov.expression.operation.Sub;
import ru.nsu.bogomolov.parser.ExpressionParser;

/** Проверяет операцию вычитания. */
class SubTest {

    /**
     * Проверяет вычисление операции над двумя константами.
     */
    @Test
    void evaluatesTwoOperands() {
        assertEquals(7, new Sub(new Number(10), new Number(3)).eval(""));
    }

    /**
     * Проверяет рекурсивное вычисление переменного операнда.
     */
    @Test
    void evaluatesVariableOperand() {
        assertEquals(7, new Sub(new Variable("x"), new Number(3)).eval("x=10"));
    }

    /**
     * Проверяет печать операции со скобками.
     */
    @Test
    void printsParenthesizedExpression() {
        assertEquals("(x-3)", new Sub(new Variable("x"), new Number(3)).toString());
    }

    /**
     * Проверяет сворачивание операции над константами.
     */
    @Test
    void simplifiesConstants() {
        assertEquals(new Number(7), new Sub(new Number(10), new Number(3)).simplify());
    }

    /**
     * Проверяет правило производной разности.
     */
    @Test
    void buildsDifferenceDerivative() {
        Expression expression = ExpressionParser.parse("x-3*x");
        assertEquals("(1-((0*x)+(3*1)))", expression.derivative("x").toString());
        assertEquals(-2, expression.derivative("x").eval("x=2"));
    }
}