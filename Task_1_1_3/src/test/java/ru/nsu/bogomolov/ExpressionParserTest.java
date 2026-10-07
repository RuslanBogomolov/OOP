package ru.nsu.bogomolov;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import ru.nsu.bogomolov.expression.Expression;
import ru.nsu.bogomolov.expression.Number;
import ru.nsu.bogomolov.expression.Variable;
import ru.nsu.bogomolov.expression.operation.Add;
import ru.nsu.bogomolov.expression.operation.Div;
import ru.nsu.bogomolov.expression.operation.Mul;
import ru.nsu.bogomolov.expression.operation.Sub;
import ru.nsu.bogomolov.parser.ExpressionParser;

/** Проверяет разбор арифметических выражений. */
class ExpressionParserTest {

    /**
     * Проверяет разбор константы.
     */
    @Test
    void parsesNumber() {
        assertEquals("12", ExpressionParser.parse("12").toString());
    }

    /**
     * Проверяет разбор многобуквенного имени.
     */
    @Test
    void parsesVariable() {
        assertEquals("long_name12", ExpressionParser.parse("long_name12").toString());
    }

    /**
     * Проверяет разбор имени с русскими буквами.
     */
    @Test
    void parsesUnicodeName() {
        assertEquals("значение2", ExpressionParser.parse("значение2").toString());
    }

    /**
     * Проверяет разбор имени, начинающегося с подчеркивания.
     */
    @Test
    void parsesUnderscoreName() {
        assertEquals("_value", ExpressionParser.parse("_value").toString());
    }

    /**
     * Проверяет приоритет умножения перед сложением.
     */
    @Test
    void respectsMultiplicationPriority() {
        assertEquals("(2+(3*4))", ExpressionParser.parse("2+3*4").toString());
    }

    /**
     * Проверяет приоритет деления перед вычитанием.
     */
    @Test
    void respectsDivisionPriority() {
        assertEquals("(8-(6/2))", ExpressionParser.parse("8-6/2").toString());
    }

    /**
     * Проверяет изменение приоритета скобками.
     */
    @Test
    void respectsParentheses() {
        assertEquals("((2+3)*4)", ExpressionParser.parse("(2+3)*4").toString());
    }

    /**
     * Проверяет вложенные скобки.
     */
    @Test
    void parsesNestedParentheses() {
        assertEquals("(x+2)", ExpressionParser.parse("(((x+2)))").toString());
    }

    @Test
    void parsesSingleVariableWithNestedParentheses() {
        assertEquals(new Variable("x"), ExpressionParser.parse("(((x)))"));
    }

    /**
     * Проверяет разбор сложения с лишними вложенными скобками.
     */
    @Test
    void parsesAdditionWithNestedParentheses() {
        assertEquals(new Add(new Variable("x"), new Variable("y")),
                ExpressionParser.parse("((x)+(y))"));
    }

    /**
     * Проверяет разбор сложения с вложенным умножением.
     */
    @Test
    void parsesAdditionWithNestedMultiplication() {
        assertEquals(new Add(new Variable("a"),
                        new Mul(new Variable("b"), new Variable("c"))),
                ExpressionParser.parse("((a)+((b)*(c)))"));
    }

    @Test
    void parsesMultiLetterVariables() {
        assertEquals(new Add(new Variable("alpha"), new Variable("beta")),
                ExpressionParser.parse("(alpha+beta)"));
        assertEquals(new Mul(new Variable("veryLongVariableName"), new Variable("short")),
                ExpressionParser.parse("(veryLongVariableName * short)"));
    }

    @Test
    void parsesFormattingSpaces() {
        Expression expected = new Add(new Variable("x"), new Variable("y"));
        assertEquals(expected, ExpressionParser.parse("( x + y )"));
        assertEquals(expected, ExpressionParser.parse("(x+ y)"));
        assertEquals(expected, ExpressionParser.parse("(x +y)"));
    }

    @Test
    void parsesNegativeNumbersInsideParentheses() {
        assertEquals(new Number(-5), ExpressionParser.parse("(-5)"));
        assertEquals(new Add(new Variable("x"), new Number(-3)),
                ExpressionParser.parse("(x + (-3))"));
    }

    @Test
    void parsesComplexExpressionTree() {
        Expression expected = new Add(
                new Mul(new Variable("a"), new Variable("b")),
                new Div(
                        new Sub(new Variable("c"), new Variable("d")),
                        new Add(new Number(1), new Number(2))));
        assertEquals(expected, ExpressionParser.parse("((a*b)+((c-d)/(1+2)))"));
    }

    /**
     * Проверяет левую ассоциативность вычитания.
     */
    @Test
    void subtractsLeftToRight() {
        assertEquals("((10-3)-2)", ExpressionParser.parse("10-3-2").toString());
    }

    /**
     * Проверяет левую ассоциативность деления.
     */
    @Test
    void dividesLeftToRight() {
        assertEquals("((20/5)/2)", ExpressionParser.parse("20/5/2").toString());
    }

    /**
     * Проверяет пропуск пробелов, табуляций и переводов строки.
     */
    @Test
    void skipsWhitespace() {
        assertEquals("(x+2)", ExpressionParser.parse(" \t x +\n 2 \t").toString());
    }

    /**
     * Проверяет разбор отрицательной константы.
     */
    @Test
    void parsesNegativeConstant() {
        assertEquals("-12", ExpressionParser.parse("-12").toString());
    }

    /**
     * Проверяет разбор унарного плюса.
     */
    @Test
    void parsesUnaryPlus() {
        assertEquals("x", ExpressionParser.parse("+x").toString());
    }

    /**
     * Проверяет разбор унарного минуса перед переменной.
     */
    @Test
    void parsesNegativeVariable() {
        assertEquals("(0-x)", ExpressionParser.parse("-x").toString());
    }

    /**
     * Проверяет разбор унарного минуса перед скобками.
     */
    @Test
    void parsesNegativeGroup() {
        assertEquals("(0-(x+2))", ExpressionParser.parse("-(x+2)").toString());
    }

    /**
     * Проверяет последовательные унарные знаки.
     */
    @Test
    void parsesRepeatedUnarySigns() {
        assertEquals("3", ExpressionParser.parse("--3").toString());
    }

    /**
     * Проверяет разбор степени с постоянным показателем.
     */
    @Test
    void parsesPower() {
        assertEquals("(x^3)", ExpressionParser.parse("x^3").toString());
    }

    /**
     * Проверяет приоритет степени перед унарным минусом.
     */
    @Test
    void powerPrecedesUnaryMinus() {
        assertEquals("(0-(x^2))", ExpressionParser.parse("-x^2").toString());
    }

    /**
     * Проверяет скобки вокруг отрицательного основания.
     */
    @Test
    void parsesPowerOfNegativeBase() {
        assertEquals("((0-x)^2)", ExpressionParser.parse("(-x)^2").toString());
    }

    /**
     * Проверяет явно заданную вложенность степеней.
     */
    @Test
    void parsesNestedPowers() {
        assertEquals("((x^2)^3)", ExpressionParser.parse("(x^2)^3").toString());
    }

    /**
     * Проверяет отказ от пустого выражения.
     */
    @Test
    void rejectsEmptyExpression() {
        assertThrows(IllegalArgumentException.class, () -> ExpressionParser.parse(""));
    }

    /**
     * Проверяет отказ от строки из пробелов.
     */
    @Test
    void rejectsWhitespaceOnly() {
        assertThrows(IllegalArgumentException.class, () -> ExpressionParser.parse("   "));
    }

    /**
     * Проверяет отсутствие операнда после операции.
     */
    @Test
    void rejectsMissingOperand() {
        assertThrows(IllegalArgumentException.class, () -> ExpressionParser.parse("x+"));
    }

    /**
     * Проверяет отсутствие закрывающей скобки.
     */
    @Test
    void rejectsUnclosedParenthesis() {
        assertThrows(IllegalArgumentException.class, () -> ExpressionParser.parse("(x+2"));
    }

    /**
     * Проверяет отказ от пустых скобок.
     */
    @Test
    void rejectsEmptyParentheses() {
        assertThrows(IllegalArgumentException.class, () -> ExpressionParser.parse("()"));
    }

    /**
     * Проверяет лишнюю закрывающую скобку.
     */
    @Test
    void rejectsExtraClosingParenthesis() {
        assertThrows(IllegalArgumentException.class, () -> ExpressionParser.parse("x)"));
    }

    /**
     * Проверяет отсутствие операции между операндами.
     */
    @Test
    void rejectsAdjacentOperands() {
        assertThrows(IllegalArgumentException.class, () -> ExpressionParser.parse("x 2"));
    }

    /**
     * Проверяет отказ от дробной константы при вычислениях int.
     */
    @Test
    void rejectsDecimalLiteral() {
        assertThrows(IllegalArgumentException.class, () -> ExpressionParser.parse("1.5"));
    }

    /**
     * Проверяет неизвестный знак операции.
     */
    @Test
    void rejectsUnknownOperation() {
        assertThrows(IllegalArgumentException.class, () -> ExpressionParser.parse("x%2"));
    }

    @Test
    void rejectsListedMalformedExpressions() {
        assertThrows(IllegalArgumentException.class, () -> ExpressionParser.parse("(x+y"));
        assertThrows(IllegalArgumentException.class, () -> ExpressionParser.parse("x+y)"));
        assertThrows(IllegalArgumentException.class, () -> ExpressionParser.parse("(x # y)"));
        assertThrows(IllegalArgumentException.class, () -> ExpressionParser.parse("()"));
        assertThrows(IllegalArgumentException.class, () -> ExpressionParser.parse("(+)"));
        assertThrows(IllegalArgumentException.class, () -> ExpressionParser.parse("(x++)"));
    }

    /**
     * Проверяет отрицательный показатель степени.
     */
    @Test
    void rejectsNegativePower() {
        assertThrows(IllegalArgumentException.class, () -> ExpressionParser.parse("x^-2"));
    }

    /**
     * Проверяет переменный показатель степени.
     */
    @Test
    void rejectsVariablePower() {
        assertThrows(IllegalArgumentException.class, () -> ExpressionParser.parse("x^y"));
    }

    /**
     * Проверяет отсутствие показателя степени.
     */
    @Test
    void rejectsMissingPower() {
        assertThrows(IllegalArgumentException.class, () -> ExpressionParser.parse("x^"));
    }

    /**
     * Проверяет отказ от цепочки степеней без скобок.
     */
    @Test
    void rejectsAmbiguousPowerChain() {
        assertThrows(IllegalArgumentException.class, () -> ExpressionParser.parse("x^2^3"));
    }

    /**
     * Проверяет константу за пределами int.
     */
    @Test
    void rejectsOutOfRangeNumber() {
        assertThrows(IllegalArgumentException.class, () -> ExpressionParser.parse("2147483648"));
    }

    /**
     * Проверяет показатель степени за пределами int.
     */
    @Test
    void rejectsOutOfRangePower() {
        assertThrows(IllegalArgumentException.class, () -> ExpressionParser.parse("x^2147483648"));
    }

    /**
     * Проверяет позицию ошибки в сообщении парсера.
     */
    @Test
    void reportsErrorPosition() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> ExpressionParser.parse("x+"));
        assertTrue(error.getMessage().contains("позиция 2"));
    }

    /**
     * Проверяет повторный разбор напечатанного выражения.
     */
    @Test
    void readsPrintedExpression() {
        Expression expression = ExpressionParser.parse("(x+2)*y^3");
        assertEquals(expression, ExpressionParser.parse(expression.toString()));
    }
}