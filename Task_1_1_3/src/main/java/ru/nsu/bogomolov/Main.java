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
     */
    public static void main(String[] args) {
        try {
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
                } catch (NumberFormatException exception) {
                    System.out.println(
                            "Значение переменной должно быть целым числом "
                                    + "в диапазоне int. Повторите ввод."
                    );
                } catch (IllegalArgumentException exception) {
                    System.out.println(exception.getMessage() + ". Повторите ввод.");
                } catch (ArithmeticException exception) {
                    System.out.println("Деление на ноль. Повторите ввод.");
                }
            }
            if (args.length > 1) {
                Files.writeString(Path.of(args[1]), expression.toString(), StandardCharsets.UTF_8);
            }
        } catch (IOException exception) {
            System.out.println(
                    "Ошибка чтения или записи файла: " + exception.getMessage()
            );
        } catch (NumberFormatException exception) {
            System.out.println(
                    "Некорректное число или число вне диапазона int."
            );
        } catch (IllegalArgumentException exception) {
            System.out.println(
                    "Ошибка в выражении: " + exception.getMessage()
            );
        } catch (ArithmeticException exception) {
            System.out.println(
                    "Ошибка при обработке выражения: " + exception.getMessage()
            );
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
                Expression expression = ExpressionParser.parse(scanner.nextLine());
                expression.simplify();
                return expression;
            } catch (IllegalArgumentException exception) {
                System.out.println("Неверное выражение: " + exception.getMessage());
                System.out.println("Пример правильной записи: 3 + 2*x");
            } catch (ArithmeticException exception) {
                System.out.println("Ошибка вычисления: " + exception.getMessage());
                System.out.println("Введите другое выражение.");
            }
        }
    }
}
