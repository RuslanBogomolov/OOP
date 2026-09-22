package ru.nsu.bogomolov.game;

import ru.nsu.bogomolov.model.Card;
import ru.nsu.bogomolov.model.Dealer;
import ru.nsu.bogomolov.model.Deck;
import ru.nsu.bogomolov.model.Player;
import ru.nsu.bogomolov.ui.ConsoleInterface;

/**
 * Консольная версия игры в блэкджек.
 * Игра продолжается до завершения пользователем.
 * За раунд выдаются карты, затем ходят игрок и дилер.
 */
public class BlackjackGame {
    private Deck deck;
    private final Player player;
    private final Dealer dealer;
    private final ConsoleInterface console;
    private final GameScore gameScore;

    /**
     * Подготавливает участников и ввод.
     */
    public BlackjackGame() {
        this(null, new Player("Игрок"), new Dealer(),
                new ConsoleInterface(), new GameScore());
    }

    /**
     * Создаёт игру с заданными зависимостями.
     *
     * @param deck колода
     * @param player игрок
     * @param dealer дилер
     * @param console консольный интерфейс
     * @param gameScore счёт игры
     */
    public BlackjackGame(Deck deck, Player player, Dealer dealer,
                         ConsoleInterface console, GameScore gameScore) {
        this.deck = deck;
        this.player = player;
        this.dealer = dealer;
        this.console = console;
        this.gameScore = gameScore;
    }

    /**
     * Создаёт игру с новой одноколодной колодой.
     *
     * @param player игрок
     * @param dealer дилер
     * @param console консольный интерфейс
     * @param gameScore счёт игры
     */
    public BlackjackGame(Player player, Dealer dealer,
                         ConsoleInterface console, GameScore gameScore) {
        this(null, player, dealer, console, gameScore);
    }

    /**
     * Запускает игровой цикл.
     */
    public void start() {
        console.println("Добро пожаловать в Блэкджек!");
        deck = new Deck(readDeckCount());

        while (true) {
            playRound();
        }
    }

    private int readDeckCount() {
        return console.readDeckCount();
    }

    private void playRound() {
        console.println("\nРаунд " + gameScore.startRound());
        player.resetHand();
        dealer.resetHand();

        dealInitialCards();

        console.println("Дилер раздал карты");
        console.printState(player, dealer, true);

        if (player.hasBlackjack() || dealer.hasBlackjack()) {
            resolveBlackjack();
            return;
        }

        if (handlePlayerTurn()) {
            gameScore.dealerWon();
            console.println("\nВы проиграли раунд! "
                    + "Превышение 21 очка.");
            printOverallScore();
            return;
        }

        if (handleDealerTurn()) {
            gameScore.playerWon();
            console.println("Вы выиграли раунд!");
            printOverallScore();
            return;
        }

        determineWinner();
    }

    /**
     * Выдаёт каждому участнику по две карты.
     */
    private void dealInitialCards() {
        player.getHand().addCard(drawCard());
        dealer.getHand().addCard(drawCard());
        player.getHand().addCard(drawCard());
        dealer.getHand().addCard(drawCard());
    }

    /**
     * Читает решения игрока и выдаёт карты.
     *
     * @return true, если игрок проиграл из-за перебора
     */
    private boolean handlePlayerTurn() {
        console.println("\nВаш ход");
        console.println("-------");

        while (true) {
            if (console.askHit()) {
                Card drawn = drawCard();
                player.getHand().addCard(drawn);
                console.println("Вы открыли карту " + drawn + " ("
                        + drawn.getNominal().getBaseValue() + ")");
                console.printState(player, dealer, true);

                if (player.isBusted()) {
                    return true;
                }
            } else {
                break;
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
        console.println("\nХод дилера");
        console.println("-------");

        Card hiddenCard = dealer.getHiddenCard();
        console.println("Дилер открывает закрытую карту " + hiddenCard
                + " (" + hiddenCard.getNominal().getBaseValue() + ")");
        console.printState(player, dealer, false);

        while (dealer.shouldHit()) {
            Card drawn = drawCard();
            dealer.getHand().addCard(drawn);
            console.println("\nДилер открывает карту " + drawn + " ("
                    + drawn.getNominal().getBaseValue() + ")");
            console.printState(player, dealer, false);

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
        console.println("\nРезультат раздачи:");
        console.printState(player, dealer, false);

        if (player.hasBlackjack() && dealer.hasBlackjack()) {
            console.println("Ничья! У обоих блэкджек.");
        } else if (player.hasBlackjack()) {
            gameScore.playerWon();
            console.println("У вас блэкджек! Вы выиграли раунд.");
        } else {
            gameScore.dealerWon();
            console.println("У дилера блэкджек! "
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
            console.println("\nВы выиграли раунд!");
        } else if (dealerScore > playerScore) {
            gameScore.dealerWon();
            console.println("\nДилер выиграл раунд!");
        } else {
            console.println("\nНичья в раунде!");
        }
        printOverallScore();
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
        console.println("Счет " + getScoreString() + ".");
    }

    /**
     * Выдаёт карту, при необходимости добавляя колоду.
     *
     * @return следующая карта
     */
    private Card drawCard() {
        while (deck.isEmpty()) {
            if (console.askAddDeck()) {
                deck.addDeck();
                console.println("Новая колода добавлена и "
                        + "перемешана.");
            } else {
                console.println("Чтобы продолжить игру, "
                        + "нужно добавить колоду.");
            }
        }
        return deck.drawCard();
    }

}