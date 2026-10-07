package ru.nsu.bogomolov;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;
import org.junit.jupiter.api.Test;
import ru.nsu.bogomolov.expression.Expression;
import ru.nsu.bogomolov.expression.Number;
import ru.nsu.bogomolov.expression.Variable;

/** Проверяет переменные в выражениях. */
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

    @Test
    void differentiatesRepeatedly() {
        Expression expression = new Variable("x");
        Expression firstDerivative = expression.derivative("x");
        assertEquals(new Number(1), firstDerivative);
        assertEquals(new Number(0), firstDerivative.derivative("x"));
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