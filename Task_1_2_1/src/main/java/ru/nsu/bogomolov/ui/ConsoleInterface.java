package ru.nsu.bogomolov.ui;

import java.util.Scanner;
import ru.nsu.bogomolov.model.Card;
import ru.nsu.bogomolov.model.Dealer;
import ru.nsu.bogomolov.model.Hand;
import ru.nsu.bogomolov.model.Nominal;
import ru.nsu.bogomolov.model.Player;

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
        while (true) {
            print("\nКолода закончилась. "
                    + "Добавить новую колоду? "
                    + "Введите \"1\" для подтверждения или \"0\" для отказа: ");
            if (!hasNextLine()) {
                throw new IllegalStateException(
                        "Ввод завершён до добавления новой колоды");
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
     * Запрашивает продолжение игры после завершения раунда.
     *
     * @return true, если нужно начать следующий раунд
     */
    public boolean askContinue() {
        while (true) {
            print("\nСыграть ещё один раунд? "
                    + "Введите \"1\" для продолжения или \"0\" для выхода: ");
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
     * Выводит состояние рук игрока и дилера.
     *
     * @param player игрок
     * @param dealer дилер
     * @param hideDealerCard нужно ли скрыть вторую карту дилера
     */
    public void printState(Player player, Dealer dealer, boolean hideDealerCard) {
        println("\tВаши карты: "
                + formatHand(player.getHand(), false));
        println("Карты дилера: "
                + formatHand(dealer.getHand(), hideDealerCard));
    }

    /**
     * Форматирует руку для вывода в консоль.
     *
     * @param hand рука
     * @param hideSecondCard нужно ли скрыть вторую карту
     * @return отформатированная рука
     */
    public String formatHand(Hand hand, boolean hideSecondCard) {
        if (hand.size() == 0) {
            return "[]";
        }

        int visibleCardCount = hideSecondCard
                ? Math.min(1, hand.size())
                : hand.size();
        int reducedAces = hand.getReducedAcesCount(visibleCardCount);
        StringBuilder result = new StringBuilder("[");

        for (int i = 0; i < visibleCardCount; i++) {
            if (i > 0) {
                result.append(", ");
            }
            Card card = hand.getCard(i);
            int value = card.getNominal().getBaseValue();
            if (card.getNominal() == Nominal.ACE) {
                value = reducedAces > 0 ? 1 : 11;
                if (value == 1) {
                    reducedAces--;
                }
            }
            result.append(card).append(" (").append(value).append(")");
        }

        if (hideSecondCard && hand.size() > 1) {
            result.append(", <закрытая карта>");
        }
        result.append("]");
        if (!hideSecondCard) {
            result.append(" > ").append(hand.calculateScore());
        }
        return result.toString();
    }
}
