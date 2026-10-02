package ru.nsu.bogomolov;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import ru.nsu.bogomolov.expression.Number;
import ru.nsu.bogomolov.expression.Variable;
import ru.nsu.bogomolov.parser.ExpressionParser;

/** Проверяет общий интерфейс выражений. */
class ExpressionTest {

    /**
     * Проверяет вычисление примера из условия.
     */
    @Test
    void evaluatesTaskExample() {
        assertEquals(23, ExpressionParser.parse("3+2*x").eval("x = 10; y = 13"));
    }

    /**
     * Проверяет пропуск пустых означиваний между разделителями.
     */
    @Test
    void ignoresEmptyAssignments() {
        assertEquals(4, new Variable("x").eval("; ; x=4; ;"));
    }

    /**
     * Проверяет вычисление константы без означиваний.
     */
    @Test
    void acceptsEmptyAssignmentsForConstant() {
        assertEquals(4, new Number(4).eval(""));
    }

    /**
     * Проверяет использование последнего значения при повторном означивании.
     */
    @Test
    void usesLastRepeatedAssignment() {
        assertEquals(7, new Variable("x").eval("x=2;x=7"));
    }

    /**
     * Проверяет различие строчных и прописных имен переменных.
     */
    @Test
    void distinguishesCase() {
        assertThrows(IllegalArgumentException.class, () -> new Variable("X").eval("x=2"));
    }

    /**
     * Проверяет отрицательное значение переменной.
     */
    @Test
    void supportsNegativeAssignment() {
        assertEquals(-4, new Variable("x").eval("x=-4"));
    }

    /**
     * Проверяет означивание без знака равенства.
     */
    @Test
    void rejectsAssignmentWithoutEquals() {
        assertThrows(IllegalArgumentException.class, () -> new Variable("x").eval("x 2"));
    }

    /**
     * Проверяет означивание с несколькими знаками равенства.
     */
    @Test
    void rejectsSeveralEquals() {
        assertThrows(IllegalArgumentException.class, () -> new Variable("x").eval("x=2=3"));
    }

    /**
     * Проверяет недопустимое имя в означивании.
     */
    @Test
    void rejectsInvalidAssignmentName() {
        assertThrows(IllegalArgumentException.class, () -> new Variable("x").eval("1x=2"));
    }

    /**
     * Проверяет отсутствие имени в означивании.
     */
    @Test
    void rejectsEmptyAssignmentName() {
        assertThrows(IllegalArgumentException.class, () -> new Variable("x").eval("=2"));
    }

    /**
     * Проверяет нецелочисленное значение в означивании.
     */
    @Test
    void rejectsNonIntegerAssignment() {
        assertThrows(NumberFormatException.class, () -> new Variable("x").eval("x=2.5"));
    }

    /**
     * Проверяет отсутствие значения после знака равенства.
     */
    @Test
    void rejectsMissingAssignmentValue() {
        assertThrows(NumberFormatException.class, () -> new Variable("x").eval("x="));
    }

    /**
     * Проверяет значение означивания за пределами int.
     */
    @Test
    void rejectsAssignmentOverflow() {
        assertThrows(NumberFormatException.class, () -> new Variable("x").eval("x=2147483648"));
    }

    /** Проверяет вывод выражения методом print в стандартный поток. */
    @Test
    void printsToStandardOutput() {
        PrintStream originalOutput = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (PrintStream capturedOutput = new PrintStream(output, true,
                StandardCharsets.UTF_8)) {
            System.setOut(capturedOutput);
            ExpressionParser.parse("3+2*x").print();
            assertEquals("(3+(2*x))" + System.lineSeparator(),
                    output.toString(StandardCharsets.UTF_8));
        } finally {
            System.setOut(originalOutput);
        }
    }

}