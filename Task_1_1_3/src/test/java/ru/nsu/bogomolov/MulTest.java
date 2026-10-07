package ru.nsu.bogomolov;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import ru.nsu.bogomolov.expression.Expression;
import ru.nsu.bogomolov.expression.Number;
import ru.nsu.bogomolov.expression.Variable;
import ru.nsu.bogomolov.expression.operation.Mul;
import ru.nsu.bogomolov.parser.ExpressionParser;

/** Проверяет операцию умножения. */
class MulTest {

    /**
     * Проверяет вычисление операции над двумя константами.
     */
    @Test
    void evaluatesTwoOperands() {
        assertEquals(30, new Mul(new Number(10), new Number(3)).eval(""));
    }

    /**
     * Проверяет рекурсивное вычисление переменного операнда.
     */
    @Test
    void evaluatesVariableOperand() {
        assertEquals(30, new Mul(new Variable("x"), new Number(3)).eval("x=10"));
    }

    /**
     * Проверяет печать операции со скобками.
     */
    @Test
    void printsParenthesizedExpression() {
        assertEquals("(x*3)", new Mul(new Variable("x"), new Number(3)).toString());
    }

    /**
     * Проверяет сворачивание операции над константами.
     */
    @Test
    void simplifiesConstants() {
        assertEquals(new Number(30), new Mul(new Number(10), new Number(3)).simplify());
    }

    /**
     * Проверяет правило производной произведения.
     */
    @Test
    void buildsProductDerivative() {
        Expression expression = ExpressionParser.parse("x*x");
        assertEquals("((1*x)+(x*1))", expression.derivative("x").toString());
        assertEquals(8, expression.derivative("x").eval("x=4"));
    }
}