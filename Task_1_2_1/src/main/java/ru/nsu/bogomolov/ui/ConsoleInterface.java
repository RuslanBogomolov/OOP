package ru.nsu.bogomolov.ui;

import ru.nsu.bogomolov.model.Dealer;
import ru.nsu.bogomolov.model.Player;

import java.util.Scanner;

/**
 * Отвечает за консольный ввод и вывод игры.
 */
public class ConsoleInterface {
    private final Scanner scanner;

    /**
     * Создаёт интерфейс, использующий стандартные потоки консоли.
     */
    public ConsoleInterface() {
        scanner = new Scanner(System.in);
    }

    /**
     * Выводит сообщение без перехода на новую строку.
     *
     * @param message сообщение
     */
    public void print(String message) {
        System.out.print(message);
    }

    /**
     * Выводит сообщение с переходом на новую строку.
     *
     * @param message сообщение
     */
    public void println(String message) {
        System.out.println(message);
    }

    /**
     * Проверяет наличие строки во входном потоке.
     *
     * @return true, если строка доступна
     */
    public boolean hasNextLine() {
        return scanner.hasNextLine();
    }

    /**
     * Читает следующую строку.
     *
     * @return введённая строка
     */
    public String readLine() {
        return scanner.nextLine();
    }

    /**
     * Запрашивает у пользователя количество колод для игры.
     *
     * @return положительное количество колод
     */
    public int readDeckCount() {
        while (true) {
            print("Введите количество колод: ");
            if (!hasNextLine()) {
                throw new IllegalStateException(
                        "Ввод завершён до указания количества колод");
            }

            String input = readLine().trim();
            try {
                int deckCount = Integer.parseInt(input);
                if (deckCount > 0) {
                    return deckCount;
                }
            } catch (NumberFormatException ignored) {
                // Некорректное значение будет обработано сообщением ниже.
            }

            println("Введите положительное целое число.");
        }
    }

    /**
     * Запрашивает решение игрока.
     *
     * @return true, если нужно взять карту; false, если нужно остановиться
     */
    public boolean askHit() {
        while (true) {
            print("Введите “1” — взять карту, "
                    + "“0” — остановиться: ");
            if (!hasNextLine()) {
                return false;
            }

            String input = readLine().trim();
            if ("1".equals(input)) {
                return true;
            }
            if ("0".equals(input)) {
                return false;
            }
            println("Неверный ввод, введите 1 или 0.");
        }
    }

    /**
     * Запрашивает подтверждение добавления новой колоды.
     *
     * @return true, если пользователь подтвердил добавление
     */
    public boolean askAddDeck() {
        print("\nКолода закончилась. "
                + "Добавить новую колоду? "
                + "Введите \"1\" для подтверждения: ");
        if (!hasNextLine()) {
            throw new IllegalStateException(
                    "Ввод завершён до добавления новой колоды");
        }
        return "1".equals(readLine().trim());
    }

    /**
     * Выводит состояние рук игрока и дилера.
     *
     * @param player игрок
     * @param dealer дилер
     * @param hideDealerCard нужно ли скрыть вторую карту дилера
     */
    public void printState(Player player, Dealer dealer, boolean hideDealerCard) {
        println("\tВаши карты: "
                + player.getHand().toDisplayString(false));
        println("Карты дилера: "
                + dealer.getHand().toDisplayString(hideDealerCard));
    }
}
