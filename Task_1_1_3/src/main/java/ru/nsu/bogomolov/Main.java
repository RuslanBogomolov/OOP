package ru.nsu.bogomolov;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;
import ru.nsu.bogomolov.expression.Expression;
import ru.nsu.bogomolov.parser.ExpressionParser;

/**
 * Читает выражение, выводит производную и вычисляет значение по введенным означиваниям.
 */
public final class Main {

    /**
     * Запускает программу с вводом выражения из консоли или файла.
     *
     * @param args необязательные пути входного и выходного файлов
     * @throws IOException если файл не удалось прочитать или записать
     */
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        Expression expression;

        if (args.length > 0) {
            String text = Files.readString(Path.of(args[0]), StandardCharsets.UTF_8);
            expression = ExpressionParser.parse(text);
        } else {
            expression = readExpression(scanner);
            if (expression == null) {
                return;
            }
        }

        System.out.println("Выражение: " + expression);
        System.out.println("Упрощенное выражение: " + expression.simplify());

        System.out.println("Переменная для дифференцирования:");
        String variable = scanner.nextLine().trim();

        System.out.println("Дифференцируем по: [" + variable + "]");

        Expression derivative = expression.derivative(variable);
        System.out.println("Производная: " + derivative);
        System.out.println("Упрощенная производная: " + derivative.simplify());

        while (true) {
            System.out.println("Задайте значения переменных (регистр важен), например x = 1; "
                    + "y = 2 или X = 2:");
            if (!scanner.hasNextLine()) {
                return;
            }
            String assignments = scanner.nextLine();
            try {
                int value = expression.eval(assignments);
                System.out.println("Значение: " + value);
                break;
            } catch (IllegalArgumentException | ArithmeticException exception) {
                System.out.println(exception.getMessage() + ". Повторите ввод.");
            }
        }
        if (args.length > 1) {
            Files.writeString(Path.of(args[1]), expression.toString(), StandardCharsets.UTF_8);
        }
    }

    /**
     * Читает выражение из консоли до первой корректной записи.
     *
     * @param scanner источник строк
     * @return разобранное выражение или {@code null} при окончании ввода
     */
    private static Expression readExpression(Scanner scanner) {
        while (true) {
            System.out.println("Введите выражение (например, 3 + 2*x):");
            if (!scanner.hasNextLine()) {
                return null;
            }
            try {
                return ExpressionParser.parse(scanner.nextLine());
            } catch (IllegalArgumentException exception) {
                System.out.println("Неверное выражение: " + exception.getMessage());
                System.out.println("Пример правильной записи: 3 + 2*x");
            }
        }
    }
}
