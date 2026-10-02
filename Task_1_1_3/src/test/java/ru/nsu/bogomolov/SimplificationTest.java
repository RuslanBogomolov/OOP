package ru.nsu.bogomolov;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;
import org.junit.jupiter.api.Test;
import ru.nsu.bogomolov.expression.Expression;
import ru.nsu.bogomolov.expression.Variable;
import ru.nsu.bogomolov.expression.operation.Mul;
import ru.nsu.bogomolov.expression.operation.Pow;
import ru.nsu.bogomolov.parser.ExpressionParser;

/** Проверяет упрощение выражений. */
class SimplificationTest {

    /**
     * Проверяет удаление нуля справа от сложения.
     */
    @Test
    void removesRightZeroFromSum() {
        assertEquals("x", ExpressionParser.parse("x+0").simplify().toString());
    }

    /**
     * Проверяет удаление нуля слева от сложения.
     */
    @Test
    void removesLeftZeroFromSum() {
        assertEquals("x", ExpressionParser.parse("0+x").simplify().toString());
    }

    /**
     * Проверяет вычитание нуля.
     */
    @Test
    void removesZeroSubtrahend() {
        assertEquals("x", ExpressionParser.parse("x-0").simplify().toString());
    }

    /**
     * Проверяет вычитание одинаковых подвыражений.
     */
    @Test
    void subtractsIdenticalTrees() {
        assertEquals("0", ExpressionParser.parse("(x+2)-(x+2)").simplify().toString());
    }

    /**
     * Проверяет умножение на ноль справа.
     */
    @Test
    void replacesRightZeroFactor() {
        assertEquals("0", ExpressionParser.parse("x*0").simplify().toString());
    }

    /**
     * Проверяет умножение на ноль слева.
     */
    @Test
    void replacesLeftZeroFactor() {
        assertEquals("0", ExpressionParser.parse("0*x").simplify().toString());
    }

    /**
     * Проверяет удаление единичного множителя справа.
     */
    @Test
    void removesRightUnitFactor() {
        assertEquals("x", ExpressionParser.parse("x*1").simplify().toString());
    }

    /**
     * Проверяет удаление единичного множителя слева.
     */
    @Test
    void removesLeftUnitFactor() {
        assertEquals("x", ExpressionParser.parse("1*x").simplify().toString());
    }

    /**
     * Проверяет рекурсивное вычисление постоянного выражения.
     */
    @Test
    void foldsConstantTree() {
        assertEquals("14", ExpressionParser.parse("2+3*4").simplify().toString());
    }

    /**
     * Проверяет объединение одинаковых слагаемых.
     */
    @Test
    void combinesEqualSummands() {
        assertEquals("(2*x)", ExpressionParser.parse("x+x").simplify().toString());
    }

    /**
     * Проверяет сложение коэффициентов одинаковых слагаемых.
     */
    @Test
    void combinesSummandCoefficients() {
        assertEquals("(5*x)", ExpressionParser.parse("2*x+3*x").simplify().toString());
    }

    /**
     * Проверяет объединение числовых множителей.
     */
    @Test
    void combinesNumericFactors() {
        assertEquals("(6*x)", ExpressionParser.parse("2*x*3").simplify().toString());
    }

    /**
     * Проверяет запись повторяющихся множителей через степень.
     */
    @Test
    void combinesRepeatedFactors() {
        assertEquals("(6*(x^2))", ExpressionParser.parse("2*x*3*x").simplify().toString());
    }

    /**
     * Проверяет сравнение произведений с разным порядком множителей.
     */
    @Test
    void ordersFactorsForComparison() {
        assertEquals("0", ExpressionParser.parse("x*y-y*x").simplify().toString());
    }

    /**
     * Проверяет объединение степени с таким же основанием.
     */
    @Test
    void combinesPowerAndVariable() {
        assertEquals("(x^3)", ExpressionParser.parse("x^2*x").simplify().toString());
    }

    /**
     * Проверяет объединение степеней с одинаковым основанием.
     */
    @Test
    void combinesTwoPowers() {
        assertEquals("(x^5)", ExpressionParser.parse("x^2*x^3").simplify().toString());
    }

    /**
     * Проверяет упрощение операндов до применения внешнего правила.
     */
    @Test
    void simplifiesNestedOperands() {
        assertEquals("x", ExpressionParser.parse("(1+0)*(x*1)").simplify().toString());
    }

    /**
     * Проверяет сохранение различных слагаемых.
     */
    @Test
    void keepsDifferentSummands() {
        assertEquals("(x+y)", ExpressionParser.parse("x+y").simplify().toString());
    }

    /**
     * Проверяет отрицательный коэффициент после вычитания.
     */
    @Test
    void keepsNegativeCoefficient() {
        assertEquals("(-3*x)", ExpressionParser.parse("2*x-5*x").simplify().toString());
    }

    /**
     * Проверяет сохранение суммы как множителя.
     */
    @Test
    void keepsCompoundFactor() {
        assertEquals("((x+1)^2)", ExpressionParser.parse("(x+1)*(x+1)").simplify().toString());
    }

    /**
     * Проверяет объединение одинаковых дробей без сокращения знаменателя.
     */
    @Test
    void combinesEqualIntegerQuotients() {
        assertEquals("(2*(x/2))", ExpressionParser.parse("x/2+x/2").simplify().toString());
    }

    /**
     * Проверяет объединение слагаемых вложенных сумм.
     */
    @Test
    void flattensNestedSums() {
        assertEquals("(6*x)", ExpressionParser.parse("x+(2*x+3*x)").simplify().toString());
    }

    /**
     * Проверяет знаки слагаемых во вложенном вычитании.
     */
    @Test
    void simplifiesNestedSubtraction() {
        assertEquals("((2*x)+(-1*y))", ExpressionParser.parse("x-(y-x)").simplify().toString());
    }

    /**
     * Проверяет сохранение исходного дерева после упрощения.
     */
    @Test
    void preservesOriginalExpression() {
        Expression original = ExpressionParser.parse("x*1");
        Expression simplified = original.simplify();
        assertEquals("x", simplified.toString());
        assertEquals("(x*1)", original.toString());
        assertNotSame(original, simplified);
    }

    /**
     * Проверяет короткую форму производной шестой степени из примера.
     */
    @Test
    void simplifiesLongDerivative() {
        Expression original = ExpressionParser.parse("X*X*X*X*X*X*3*6*3+3*6*4");
        final String text = original.toString();
        Expression derivative = original.derivative("X").simplify();
        assertEquals("(324*(X^5))", derivative.toString());
        assertEquals(3528, original.eval("X=2"));
        assertEquals(10368, derivative.eval("X=2"));
        assertEquals(text, original.toString());
    }

    /**
     * Проверяет устойчивость результата повторного упрощения.
     */
    @Test
    void isIdempotent() {
        Expression simplified = ExpressionParser.parse("2*x*3*x+4*x^2-10*x^2+y").simplify();
        assertEquals(simplified, simplified.simplify());
    }

    /**
     * Сравнивает значения до и после упрощения на нескольких означиваниях.
     */
    @Test
    void preservesValuesForSeveralAssignments() {
        String[] sources = {"2*x+3*x-4*x+6", "x*y+y*x", "(x+y)*(y+x)",
            "x*x*x+x*x*x", "(x+1)^3", "x/2+x/2", "3*(x/2)*2", "x-(y-x)"};
        for (String source : sources) {
            Expression original = ExpressionParser.parse(source);
            Expression simplified = original.simplify();
            for (int x = -3; x <= 3; x++) {
                for (int y = -2; y <= 2; y++) {
                    Map<String, Integer> values = Map.of("x", x, "y", y);
                    assertEquals(original.evaluate(values), simplified.evaluate(values),
                            () -> "Изменилось значение выражения " + source + " при " + values);
                }
            }
        }
    }

    /**
     * Проверяет переполнение при объединении показателей множителей.
     */
    @Test
    void rejectsMergedExponentOverflow() {
        Expression expression = new Mul(new Pow(new Variable("x"), Integer.MAX_VALUE),
                new Variable("x"));
        assertThrows(ArithmeticException.class, expression::simplify);
    }
}