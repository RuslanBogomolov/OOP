package ru.nsu.bogomolov;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import ru.nsu.bogomolov.expression.Expression;
import ru.nsu.bogomolov.expression.Number;
import ru.nsu.bogomolov.expression.Variable;
import ru.nsu.bogomolov.expression.operation.Add;
import ru.nsu.bogomolov.parser.ExpressionParser;

/** Проверяет операцию сложения. */
class AddTest {

    /**
     * Проверяет вычисление операции над двумя константами.
     */
    @Test
    void evaluatesTwoOperands() {
        assertEquals(13, new Add(new Number(10), new Number(3)).eval(""));
    }

    /**
     * Проверяет рекурсивное вычисление переменного операнда.
     */
    @Test
    void evaluatesVariableOperand() {
        assertEquals(13, new Add(new Variable("x"), new Number(3)).eval("x=10"));
    }

    /**
     * Проверяет печать операции со скобками.
     */
    @Test
    void printsParenthesizedExpression() {
        assertEquals("(x+3)", new Add(new Variable("x"), new Number(3)).toString());
    }

    /**
     * Проверяет сворачивание операции над константами.
     */
    @Test
    void simplifiesConstants() {
        assertEquals(new Number(13), new Add(new Number(10), new Number(3)).simplify());
    }

    /**
     * Проверяет правило производной суммы.
     */
    @Test
    void buildsSumDerivative() {
        Expression expression = ExpressionParser.parse("x+3*x");
        assertEquals("(1+((0*x)+(3*1)))", expression.derivative("x").toString());
        assertEquals(4, expression.derivative("x").eval("x=2"));
    }
}