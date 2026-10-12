package ru.nsu.bogomolov.parser;

import ru.nsu.bogomolov.expression.Expression;
import ru.nsu.bogomolov.expression.Number;
import ru.nsu.bogomolov.expression.Variable;
import ru.nsu.bogomolov.expression.operation.Add;
import ru.nsu.bogomolov.expression.operation.Div;
import ru.nsu.bogomolov.expression.operation.Mul;
import ru.nsu.bogomolov.expression.operation.Pow;
import ru.nsu.bogomolov.expression.operation.Sub;

/**
 * Разбирает выражения со скобками, приоритетами операций и многобуквенными именами.
 */
public final class ExpressionParser {
    /** Исходная строка выражения. */
    private final String text;
    /** Индекс следующего непрочитанного символа. */
    private int position;

    /**
     * Создает парсер и устанавливает позицию чтения в начало строки.
     *
     * @param text исходная строка выражения
     */
    private ExpressionParser(String text) {
        this.text = text;
    }

    /**
     * Создает дерево выражения из строки и проверяет, что строка разобрана полностью.
     *
     * @param text исходная строка
     * @return дерево выражения
     * @throws IllegalArgumentException если строка содержит синтаксическую ошибку или число вне
     *     диапазона int
     */
    public static Expression parse(String text) {
        ExpressionParser parser = new ExpressionParser(text);
        Expression result = parser.parseSum();
        parser.skipSpaces();
        if (parser.position != text.length()) {
            throw parser.error("Лишний символ");
        }
        return result;
    }

    /**
     * Читает сложения и вычитания слева направо.
     *
     * @return дерево суммы или разности
     */
    private Expression parseSum() {
        Expression result = parseProduct();
        while (true) {
            if (take('+')) {
                result = new Add(result, parseProduct());
            } else if (take('-')) {
                result = new Sub(result, parseProduct());
            } else {
                return result;
            }
        }
    }

    /**
     * Читает умножения и деления слева направо.
     *
     * @return дерево произведения или частного
     */
    private Expression parseProduct() {
        Expression result = parsePrimary();
        while (true) {
            if (take('*')) {
                result = new Mul(result, parsePrimary());
            } else if (take('/')) {
                result = new Div(result, parsePrimary());
            } else {
                return result;
            }
        }
    }

    /**
     * Читает унарный знак и степень; степень имеет больший приоритет, чем знак.
     *
     * @return дерево выражения с унарным знаком или степенью
     */
    private Expression parsePrimary() {
        if (take('-')) {
            Expression operand = parsePrimary();
            if (operand instanceof Number) {
                return new Number(-((Number) operand).getValue());
            }
            return new Sub(new Number(0), operand);
        }
        if (take('+')) {
            return parsePrimary();
        }
        Expression result = parseAtom();
        if (take('^')) {
            skipSpaces();
            int start = position;
            while (position < text.length() && isDigit(text.charAt(position))) {
                position++;
            }
            if (start == position) {
                throw error("Ожидался целый неотрицательный показатель степени");
            }
            result = new Pow(result, Integer.parseInt(text.substring(start, position)));
        }
        return result;
    }

    /**
     * Читает число, переменную или выражение в скобках.
     *
     * @return дерево прочитанного выражения
     */
    private Expression parseAtom() {
        if (take('(')) {
            Expression result = parseSum();
            if (!take(')')) {
                throw error("Ожидалась закрывающая скобка");
            }
            return result;
        }
        skipSpaces();
        int start = position;
        if (position < text.length() && isDigit(text.charAt(position))) {
            while (position < text.length() && isDigit(text.charAt(position))) {
                position++;
            }
            return new Number(Integer.parseInt(text.substring(start, position)));
        }
        if (position < text.length()
                && (Character.isLetter(text.charAt(position)) || text.charAt(position) == '_')) {
            while (position < text.length()
                    && (Character.isLetterOrDigit(text.charAt(position))
                    || text.charAt(position) == '_')) {
                position++;
            }
            return new Variable(text.substring(start, position));
        }
        throw error("Ожидалось число, переменная или скобка");
    }

    /**
     * Пропускает пробелы и считывает ожидаемый символ, если он присутствует.
     *
     * @param expected ожидаемый символ
     * @return true, если символ был считан
     */
    private boolean take(char expected) {
        skipSpaces();
        if (position < text.length() && text.charAt(position) == expected) {
            position++;
            return true;
        }
        return false;
    }

    /**
     * Перемещает позицию чтения за последовательность пробельных символов.
     */
    private void skipSpaces() {
        while (position < text.length() && Character.isWhitespace(text.charAt(position))) {
            position++;
        }
    }

    /**
     * Проверяет, является ли символ десятичной цифрой от 0 до 9.
     *
     * @param symbol проверяемый символ
     * @return true для десятичной цифры
     */
    private static boolean isDigit(char symbol) {
        return symbol >= '0' && symbol <= '9';
    }

    /**
     * Создает сообщение об ошибке с текущей позицией в строке.
     *
     * @param message описание ошибки
     * @return исключение с описанием и позицией ошибки
     */
    private IllegalArgumentException error(String message) {
        return new IllegalArgumentException(message + ", позиция " + position);
    }
}
