package ru.nsu.bogomolov;

import java.util.Scanner;

/**
 * Консольная версия игры в блэкджек.
 * Игра продолжается до завершения пользователем.
 * За раунд выдаются карты, затем ходят игрок и дилер.
 */
public class Main {
    private Deck deck;
    private final Player player;
    private final Dealer dealer;
    private final Scanner scanner;
    private final GameScore gameScore;

    /**
     * Подготавливает колоду, участников и ввод.
     */
    public Main() {
        this.deck = new Deck(1);
        this.player = new Player("Игрок");
        this.dealer = new Dealer();
        this.scanner = new Scanner(System.in);
        this.gameScore = new GameScore();
    }

    /**
     * Запускает игровой цикл.
     */
    public void start() {
        System.out.println("Добро пожаловать в Блэкджек!");
        deck = new Deck(readDeckCount());

        while (true) {
            playRound();
        }
    }

    /**
     * Запрашивает у пользователя количество колод для игры.
     *
     * @return положительное количество колод
     */
    private int readDeckCount() {
        while (true) {
            System.out.print("Введите количество колод: ");
            if (!scanner.hasNextLine()) {
                throw new IllegalStateException(
                        "Ввод завершён до указания количества колод");
            }

            String input = scanner.nextLine().trim();
            try {
                int deckCount = Integer.parseInt(input);
                if (deckCount > 0) {
                    return deckCount;
                }
            } catch (NumberFormatException ignored) {
                // Некорректное значение будет обработано сообщением ниже.
            }

            System.out.println("Введите положительное целое число.");
        }
    }

    private void playRound() {
        System.out.println("\nРаунд " + gameScore.startRound());
        player.resetHand();
        dealer.resetHand();

        // Сначала каждому участнику выдаются две карты.
        player.getHand().addCard(drawCard());
        dealer.getHand().addCard(drawCard());
        player.getHand().addCard(drawCard());
        dealer.getHand().addCard(drawCard());

        System.out.println("Дилер раздал карты");
        printState(true);

        if (player.hasBlackjack() || dealer.hasBlackjack()) {
            resolveBlackjack();
            return;
        }

        boolean playerBusted = handlePlayerTurn();

        if (playerBusted) {
            gameScore.dealerWon();
            System.out.println("\nВы проиграли раунд! "
                    + "Превышение 21 очка.");
            printOverallScore();
            return;
        }

        boolean dealerBusted = handleDealerTurn();

        if (dealerBusted) {
            gameScore.playerWon();
            System.out.println("Вы выиграли раунд! Счет "
                    + getScoreString() + " в вашу пользу.");
            return;
        }

        determineWinner();
    }

    /**
     * Читает решения игрока и выдаёт карты.
     *
     * @return true, если игрок проиграл из-за перебора
     */
    private boolean handlePlayerTurn() {
        System.out.println("\nВаш ход");
        System.out.println("-------");

        while (true) {
            System.out.print("Введите “1” — взять карту, "
                    + "“0” — остановиться: ");
            if (!scanner.hasNextLine()) {
                return false;
            }
            String input = scanner.nextLine().trim();

            if ("1".equals(input)) {
                Card drawn = drawCard();
                player.getHand().addCard(drawn);
                System.out.println("Вы открыли карту " + drawn + " ("
                        + drawn.getNominal().getBaseValue() + ")");
                printState(true);

                if (player.isBusted()) {
                    return true;
                }
            } else if ("0".equals(input)) {
                break;
            } else {
                System.out.println("Неверный ввод, введите 1 или 0.");
            }
        }
        return false;
    }

    /**
     * Открывает вторую карту дилера и выполняет его ход.
     *
     * @return true, если дилер набрал больше 21
     */
    private boolean handleDealerTurn() {
        System.out.println("\nХод дилера");
        System.out.println("-------");

        Card hiddenCard = dealer.getHand().getCards().get(1);
        System.out.println("Дилер открывает закрытую карту " + hiddenCard
                + " (" + hiddenCard.getNominal().getBaseValue() + ")");
        printState(false);

        while (dealer.shouldHit()) {
            Card drawn = drawCard();
            dealer.getHand().addCard(drawn);
            System.out.println("\nДилер открывает карту " + drawn + " ("
                    + drawn.getNominal().getBaseValue() + ")");
            printState(false);

            if (dealer.isBusted()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Обрабатывает блэкджек участников.
     */
    private void resolveBlackjack() {
        System.out.println("\nРезультат раздачи:");
        printState(false);

        if (player.hasBlackjack() && dealer.hasBlackjack()) {
            System.out.println("Ничья! У обоих блэкджек.");
        } else if (player.hasBlackjack()) {
            gameScore.playerWon();
            System.out.println("У вас блэкджек! Вы выиграли раунд. "
                    + "Счет "
                    + getScoreString() + " в вашу пользу.");
        } else {
            gameScore.dealerWon();
            System.out.println("У дилера блэкджек! "
                    + "Вы проиграли раунд.");
        }
        printOverallScore();
    }

    /**
     * Сравнивает очки и увеличивает счёт победителя.
     */
    private void determineWinner() {
        int playerScore = player.getScore();
        int dealerScore = dealer.getScore();

        if (playerScore > dealerScore) {
            gameScore.playerWon();
            System.out.println("\nВы выиграли раунд! Счет "
                    + getScoreString() + " в вашу пользу.");
        } else if (dealerScore > playerScore) {
            gameScore.dealerWon();
            System.out.println("\nДилер выиграл раунд! Счет "
                    + getScoreString() + ".");
        } else {
            System.out.println("\nНичья в раунде! Счет остается "
                    + getScoreString() + ".");
        }
    }

    /**
     * Печатает карты обоих участников.
     *
     * @param hideDealerCard нужно ли скрыть вторую карту дилера
     */
    private void printState(boolean hideDealerCard) {
        System.out.println("\tВаши карты: "
                + player.getHand().toDisplayString(false));
        System.out.println("Карты дилера: "
                + dealer.getHand().toDisplayString(hideDealerCard));
    }

    /**
     * Возвращает текущий счёт.
     *
     * @return счёт в формате «победы игрока:победы дилера»
     */
    private String getScoreString() {
        return gameScore.toString();
    }

    /**
     * Печатает текущий счёт после завершения раунда.
     */
    private void printOverallScore() {
        System.out.println("Счет " + getScoreString() + ".");
    }

    /**
     * Выдаёт карту, при необходимости добавляя колоду.
     *
     * @return следующая карта
     */
    private Card drawCard() {
        while (deck.isEmpty()) {
            System.out.print("\nКолода закончилась. "
                    + "Добавить новую колоду? "
                    + "Введите \"1\" для подтверждения: ");
            if (!scanner.hasNextLine()) {
                throw new IllegalStateException(
                        "Ввод завершён до добавления новой "
                                + "колоды");
            }
            String input = scanner.nextLine().trim();

            if ("1".equals(input)) {
                deck.addDeck();
                System.out.println("Новая колода добавлена и "
                        + "перемешана.");
            } else {
                System.out.println("Чтобы продолжить игру, "
                        + "нужно добавить колоду.");
            }
        }
        return deck.drawCard();
    }

    /**
     * Точка входа в приложение.
     *
     * @param args аргументы командной строки, не используются
     */
    public static void main(String[] args) {
        new Main().start();
    }
}