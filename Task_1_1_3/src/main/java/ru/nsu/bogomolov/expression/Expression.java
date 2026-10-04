package ru.nsu.bogomolov.expression;

import java.util.HashMap;
import java.util.Map;
import ru.nsu.bogomolov.expression.operation.Simplifier;

/**
 * Математическое выражение, которое можно вычислить, продифференцировать и упростить.
 */
public abstract class Expression {
    /**
     * Вычисляет значение выражения по заданным значениям переменных.
     *
     * @param variables соответствие имен переменных их значениям
     * @return значение выражения
     */
    public abstract int evaluate(Map<String, Integer> variables);

    /**
     * Строит производную по указанной переменной без изменения исходного выражения.
     *
     * @param variable имя переменной дифференцирования
     * @return дерево производной
     */
    public abstract Expression derivative(String variable);

    /**
     * Упрощает выражение без изменения исходного дерева.
     *
     * @return результат упрощения
     */
    public abstract Expression simplify();

    /**
     * Добавляет выражение в накопитель множителей как неделимый множитель.
     *
     * @param product накопитель множителей
     */
    public void collectFactors(Simplifier.Product product) {
        product.addFactor(this, 1);
    }

    /**
     * Добавляет выражение в накопитель слагаемых как один неделимый член.
     *
     * @param negative true, если слагаемое находится под отрицательным знаком
     * @param sum накопитель слагаемых
     */
    public void collectTerms(boolean negative, Simplifier.Sum sum) {
        Simplifier.Product product = new Simplifier.Product();
        collectFactors(product);
        int sign = negative ? -1 : 1;
        if (product.isConstant()) {
            sum.addConstant(sign * product.coefficient());
        } else {
            sum.addTerm(product.factorsExpression(), sign * product.coefficient());
        }
    }

    /**
     * Возвращает запись выражения со скобками вокруг операций.
     *
     * @return строковое представление выражения
     */
    @Override
    public abstract String toString();

    /**
     * Печатает выражение в стандартный поток вывода.
     */
    public final void print() {
        System.out.println(this);
    }

    /**
     * Разбирает означивания вида «x = 10; y = 13» и вычисляет выражение.
     *
     * @param assignments значения переменных, разделенные точкой с запятой
     * @return значение выражения
     * @throws IllegalArgumentException если означивание некорректно или переменная не задана
     * @throws ArithmeticException при делении на ноль
     */
    public final int eval(String assignments) {
        Map<String, Integer> variables = new HashMap<>();
        for (String assignment : assignments.split(";")) {
            if (assignment.trim().isEmpty()) {
                continue;
            }
            String[] parts = assignment.split("=", -1);
            if (parts.length != 2) {
                throw new IllegalArgumentException("Неверное означивание: " + assignment);
            }
            String name = parts[0].trim();
            VariableNames.validate(name);
            variables.put(name, Integer.parseInt(parts[1].trim()));
        }
        return evaluate(variables);
    }
}
