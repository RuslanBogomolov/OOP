package ru.nsu.bogomolov;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ru.nsu.bogomolov.expression.Expression;
import ru.nsu.bogomolov.expression.Number;
import ru.nsu.bogomolov.expression.Variable;
import ru.nsu.bogomolov.expression.operation.Add;
import ru.nsu.bogomolov.expression.operation.Div;
import ru.nsu.bogomolov.expression.operation.Mul;
import ru.nsu.bogomolov.expression.operation.Pow;
import ru.nsu.bogomolov.expression.operation.Sub;
import ru.nsu.bogomolov.parser.ExpressionParser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Проверки класса Add и связанных с ним правил.
 */
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

/**
 * Проверки класса BinaryExpression и связанных с ним правил.
 */
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

/**
 * Проверки класса Div и связанных с ним правил.
 */
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

/**
 * Проверки класса ExpressionParser и связанных с ним правил.
 */
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

/**
 * Проверки класса Expression и связанных с ним правил.
 */
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

/** Проверки консольного ввода и работы программы с файлами. */
class MainTest {
    /** Временная папка для входных и выходных файлов теста. */
    @TempDir
    Path directory;

    /**
     * Проверяет полный запуск программы с вводом из консоли.
     *
     * @throws IOException если операция ввода или вывода завершилась ошибкой
     */
    @Test
    void evaluatesConsoleExpression() throws IOException {
        String output = runMain("3+2*x\nx\nx=10\n");
        assertTrue(output.contains("Выражение: (3+(2*x))"));
        assertTrue(output.contains("Упрощенная производная: 2"));
        assertTrue(output.contains("Значение: 23"));
    }

    /**
     * Проверяет повторный запрос после пустого или неверного выражения.
     *
     * @throws IOException если операция ввода или вывода завершилась ошибкой
     */
    @Test
    void retriesInvalidExpression() throws IOException {
        String output = runMain("\n3+2*x\nx\nx=10\n");
        assertTrue(output.contains("Неверное выражение:"));
        assertTrue(output.contains("Пример правильной записи: 3 + 2*x"));
        assertTrue(output.contains("Значение: 23"));
    }

    /**
     * Проверяет повторный запрос после пустого означивания.
     *
     * @throws IOException если операция ввода или вывода завершилась ошибкой
     */
    @Test
    void retriesEmptyAssignment() throws IOException {
        String output = runMain("X*X\nX\n\nX=2\n");
        assertTrue(output.contains("Не задана переменная: X. Повторите ввод."));
        assertTrue(output.contains("Значение: 4"));
    }

    /**
     * Проверяет повторный запрос при неверном регистре имени.
     *
     * @throws IOException если операция ввода или вывода завершилась ошибкой
     */
    @Test
    void retriesWrongVariableCase() throws IOException {
        String output = runMain("X\nX\nx=2\nX=2\n");
        assertTrue(output.contains("Не задана переменная: X"));
        assertTrue(output.contains("Значение: 2"));
    }

    /**
     * Проверяет повторный запрос после неправильной записи означивания.
     *
     * @throws IOException если операция ввода или вывода завершилась ошибкой
     */
    @Test
    void retriesMalformedAssignment() throws IOException {
        String output = runMain("x\nx\nx 2\nx=2\n");
        assertTrue(output.contains("Неверное означивание: x 2"));
        assertTrue(output.contains("Значение: 2"));
    }

    /**
     * Проверяет повторный запрос после деления на ноль при вычислении.
     *
     * @throws IOException если операция ввода или вывода завершилась ошибкой
     */
    @Test
    void retriesZeroDenominator() throws IOException {
        String output = runMain("1/x\nx\nx=0\nx=2\n");
        assertTrue(output.contains("Повторите ввод."));
        assertTrue(output.contains("Значение: 0"));
    }

    /**
     * Проверяет завершение при окончании входного потока.
     *
     * @throws IOException если операция ввода или вывода завершилась ошибкой
     */
    @Test
    void stopsAtEndOfInput() throws IOException {
        String output = runMain("x\nx\n");
        assertFalse(output.contains("Значение:"));
    }

    /**
     * Проверяет чтение выражения из файла и запись его строковой формы.
     *
     * @throws IOException если тестовые файлы не удалось прочитать или записать
     */
    @Test
    void readsAndWritesExpressionFiles() throws IOException {
        Path input = directory.resolve("input.txt");
        Path output = directory.resolve("output.txt");
        Files.writeString(input, "3 + 2*x", StandardCharsets.UTF_8);
        String console = runMain("x\nx=10\n", input.toString(), output.toString());
        assertTrue(console.contains("Значение: 23"));
        assertEquals("(3+(2*x))", Files.readString(output, StandardCharsets.UTF_8));
    }

    /**
     * Проверяет чтение входного файла без указания выходного файла.
     *
     * @throws IOException если тестовый файл не удалось прочитать или записать
     */
    @Test
    void readsFileWithoutOutputPath() throws IOException {
        Path input = directory.resolve("input.txt");
        Files.writeString(input, "4", StandardCharsets.UTF_8);
        assertTrue(runMain("x\n\n", input.toString()).contains("Значение: 4"));
    }

    /** Проверяет сообщение об отсутствующем входном файле. */
    @Test
    void reportsMissingInputFile() {
        Path input = directory.resolve("missing.txt");
        assertThrows(IOException.class, () -> runMain("", input.toString()));
    }

    /**
     * Запускает программу с заданным вводом и перехватывает текст вывода.
     * После запуска восстанавливает стандартные потоки.
     *
     * @param input строки, которые программа прочитает из консоли
     * @param args аргументы командной строки
     * @return текст, записанный программой в стандартный вывод
     * @throws IOException если программа не смогла прочитать или записать файл
     */
    private String runMain(String input, String... args) throws IOException {
        InputStream originalInput = System.in;
        PrintStream originalOutput = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (PrintStream capturedOutput = new PrintStream(output, true, StandardCharsets.UTF_8)) {
            System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
            System.setOut(capturedOutput);
            Main.main(args);
            return output.toString(StandardCharsets.UTF_8);
        } finally {
            System.setIn(originalInput);
            System.setOut(originalOutput);
        }
    }
}

/**
 * Проверки класса Mul и связанных с ним правил.
 */
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

/**
 * Проверки класса Number и связанных с ним правил.
 */
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

/**
 * Проверки класса Pow и связанных с ним правил.
 */
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

/**
 * Проверки правил упрощения и сохранения значений выражений.
 */
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
        String text = original.toString();
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

/**
 * Проверки класса Sub и связанных с ним правил.
 */
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

/**
 * Проверки класса Variable и связанных с ним правил.
 */
class VariableTest {

    /**
     * Проверяет вычисление переменной по ее полному имени.
     */
    @Test
    void readsValueFromMap() {
        assertEquals(13, new Variable("long_name").evaluate(Map.of("long_name", 13)));
    }

    /**
     * Проверяет сообщение об отсутствующем означивании.
     */
    @Test
    void rejectsMissingValue() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> new Variable("x").evaluate(Map.of("xx", 3)));
        assertEquals("Не задана переменная: x", error.getMessage());
    }

    /**
     * Проверяет единичную производную по самой переменной.
     */
    @Test
    void differentiatesByItsOwnName() {
        assertEquals(new Number(1), new Variable("long_name").derivative("long_name"));
    }

    /**
     * Проверяет нулевую производную по другой переменной.
     */
    @Test
    void differentiatesByAnotherName() {
        assertEquals(new Number(0), new Variable("long_name").derivative("x"));
    }

    /**
     * Проверяет создание упрощенной переменной с тем же именем.
     */
    @Test
    void simplifiesWithoutChangingName() {
        Variable original = new Variable("x");
        Expression simplified = original.simplify();
        assertEquals(original, simplified);
        assertNotSame(original, simplified);
        assertEquals("x", original.toString());
    }

    /**
     * Проверяет допустимое многобуквенное имя с цифрами и подчеркиванием.
     */
    @Test
    void acceptsLettersDigitsAndUnderscore() {
        assertEquals("_значение12", new Variable("_значение12").toString());
    }

    /**
     * Проверяет отказ от отсутствующего имени переменной.
     */
    @Test
    void rejectsNullName() {
        assertThrows(IllegalArgumentException.class, () -> new Variable(null));
    }

    /**
     * Проверяет отказ от пустого имени переменной.
     */
    @Test
    void rejectsEmptyName() {
        assertThrows(IllegalArgumentException.class, () -> new Variable(""));
    }

    /**
     * Проверяет отказ от имени, начинающегося с цифры.
     */
    @Test
    void rejectsNameStartingWithDigit() {
        assertThrows(IllegalArgumentException.class, () -> new Variable("1x"));
    }

    /**
     * Проверяет отказ от имени, содержащего знак операции.
     */
    @Test
    void rejectsNameWithOperation() {
        assertThrows(IllegalArgumentException.class, () -> new Variable("x+y"));
    }

    /**
     * Проверяет равенство имен и согласованность хеш-кодов переменных.
     */
    @Test
    void comparesNamesAndHashCodes() {
        Variable first = new Variable("x");
        Variable second = new Variable("x");
        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertNotEquals(first, new Variable("X"));
    }

    /**
     * Проверяет сравнение переменной с константой.
     */
    @Test
    void doesNotEqualAnotherType() {
        assertNotEquals(new Variable("x"), new Number(1));
    }

    /**
     * Проверяет сравнение переменной с null.
     */
    @Test
    void doesNotEqualNull() {
        assertFalse(new Variable("x").equals(null));
    }
}
