package ru.nsu.bogomolov;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import ru.nsu.bogomolov.game.BlackjackGame;
import ru.nsu.bogomolov.game.GameScore;
import ru.nsu.bogomolov.model.Card;
import ru.nsu.bogomolov.model.Dealer;
import ru.nsu.bogomolov.model.Deck;
import ru.nsu.bogomolov.model.Hand;
import ru.nsu.bogomolov.model.Nominal;
import ru.nsu.bogomolov.model.Player;
import ru.nsu.bogomolov.model.Suit;
import ru.nsu.bogomolov.ui.ConsoleInterface;

/**
 * Проверяет правила блэкджека и игровые объекты.
 */
class MainTest {
    private static final InputStream ORIGINAL_INPUT = System.in;
    private static final PrintStream ORIGINAL_OUTPUT = System.out;

    /**
     * Тесты меняют стандартные потоки.
     * После каждого теста потоки возвращаются.
     */
    @AfterEach
    void restoreConsole() {
        System.setIn(ORIGINAL_INPUT);
        System.setOut(ORIGINAL_OUTPUT);
    }

    @Test
    void cardStoresSuitNominalAndText() {
        Card card = new Card(Suit.SPADES, Nominal.QUEEN);

        assertEquals(Suit.SPADES, card.getSuit());
        assertEquals(Nominal.QUEEN, card.getNominal());
        assertEquals("Дама ♠️", card.toString());
    }

    @Test
    void nominalValuesMatchBlackjackRules() {
        assertEquals(2, Nominal.TWO.getBaseValue());
        assertEquals(10, Nominal.TEN.getBaseValue());
        assertEquals(10, Nominal.JACK.getBaseValue());
        assertEquals(10, Nominal.QUEEN.getBaseValue());
        assertEquals(10, Nominal.KING.getBaseValue());
        assertEquals(11, Nominal.ACE.getBaseValue());
        assertEquals("Туз", Nominal.ACE.getName());
    }

    @Test
    void everySuitHasDisplaySymbol() {
        for (Suit suit : Suit.values()) {
            assertNotNull(suit.getSymbol());
            assertFalse(suit.getSymbol().isBlank());
        }
    }

    @Test
    void oneDeckContainsEveryCardCombinationOnce() {
        Deck deck = new Deck(1);
        Set<String> cards = new HashSet<>();

        for (int i = 0; i < 52; i++) {
            Card card = deck.drawCard();
            cards.add(card.getSuit().name() + card.getNominal().name());
        }

        assertEquals(52, cards.size());
        assertTrue(deck.isEmpty());
    }

    @Test
    void twoDecksContain104Cards() {
        Deck deck = new Deck(2);
        int cardCount = 0;

        while (!deck.isEmpty()) {
            deck.drawCard();
            cardCount++;
        }

        assertEquals(104, cardCount);
    }

    @Test
    void addingDeckMakes52CardsAvailable() {
        Deck deck = new Deck(1);
        for (int i = 0; i < 52; i++) {
            deck.drawCard();
        }

        deck.addDeck();

        int cardCount = 0;
        while (!deck.isEmpty()) {
            deck.drawCard();
            cardCount++;
        }
        assertEquals(52, cardCount);
    }

    @Test
    void addingDeckKeepsCardsThatWereNotDrawn() {
        Deck deck = new Deck(1);
        deck.drawCard();
        deck.addDeck();

        int cardCount = 0;
        while (!deck.isEmpty()) {
            deck.drawCard();
            cardCount++;
        }

        assertEquals(103, cardCount);
    }

    @Test
    void zeroDeckCanBeFilledLater() {
        Deck deck = new Deck(0);

        assertTrue(deck.isEmpty());
        deck.addDeck();

        int cardCount = 0;
        while (!deck.isEmpty()) {
            deck.drawCard();
            cardCount++;
        }
        assertEquals(52, cardCount);
    }

    @Test
    void resetRestoresOriginalDeckSize() {
        Deck deck = new Deck(1);
        deck.drawCard();
        deck.drawCard();

        deck.reset();

        int cardCount = 0;
        while (!deck.isEmpty()) {
            deck.drawCard();
            cardCount++;
        }
        assertEquals(52, cardCount);
    }

    @Test
    void emptyDeckCannotProvideCard() {
        Deck deck = new Deck(1);
        for (int i = 0; i < 52; i++) {
            deck.drawCard();
        }

        assertThrows(IllegalStateException.class, deck::drawCard);
    }

    @Test
    void emptyHandHasZeroScoreAndEmptyDisplay() {
        Hand hand = new Hand();

        assertEquals(0, hand.calculateScore());
        assertEquals(0, hand.getReducedAcesCount());
        assertEquals("[]", hand.toDisplayString(false));
    }

    @Test
    void handSumsNumberCards() {
        Hand hand = new Hand();
        hand.addCard(new Card(Suit.HEARTS, Nominal.SEVEN));
        hand.addCard(new Card(Suit.CLUBS, Nominal.FOUR));

        assertEquals(11, hand.calculateScore());
        assertEquals("[Семерка ♥️ (7), Четверка ♣️ (4)] > 11",
                hand.toDisplayString(false));
    }

    @Test
    void faceCardsAreWorthTen() {
        Hand hand = new Hand();
        hand.addCard(new Card(Suit.HEARTS, Nominal.JACK));
        hand.addCard(new Card(Suit.DIAMONDS, Nominal.QUEEN));
        hand.addCard(new Card(Suit.SPADES, Nominal.KING));

        assertEquals(30, hand.calculateScore());
        assertEquals(0, hand.getReducedAcesCount());
    }

    @Test
    void aceIsElevenWhenHandDoesNotBust() {
        Hand hand = new Hand();
        hand.addCard(new Card(Suit.HEARTS, Nominal.ACE));
        hand.addCard(new Card(Suit.CLUBS, Nominal.SIX));

        assertEquals(17, hand.calculateScore());
        assertEquals(0, hand.getReducedAcesCount());
        assertTrue(hand.toDisplayString(false).contains("Туз ♥️ (11)"));
    }

    @Test
    void aceIsReducedToOneWhenNecessary() {
        Hand hand = new Hand();
        hand.addCard(new Card(Suit.HEARTS, Nominal.ACE));
        hand.addCard(new Card(Suit.CLUBS, Nominal.NINE));
        hand.addCard(new Card(Suit.SPADES, Nominal.FIVE));

        assertEquals(15, hand.calculateScore());
        assertEquals(1, hand.getReducedAcesCount());
        assertTrue(hand.toDisplayString(false).contains("Туз ♥️ (1)"));
    }

    @Test
    void severalAcesAreReducedOnlyAsNeeded() {
        Hand hand = new Hand();
        hand.addCard(new Card(Suit.HEARTS, Nominal.ACE));
        hand.addCard(new Card(Suit.CLUBS, Nominal.ACE));
        hand.addCard(new Card(Suit.SPADES, Nominal.NINE));

        assertEquals(21, hand.calculateScore());
        assertEquals(1, hand.getReducedAcesCount());
    }

    @Test
    void hiddenCardIsExcludedFromDisplayedScore() {
        Hand hand = new Hand();
        hand.addCard(new Card(Suit.HEARTS, Nominal.TEN));
        hand.addCard(new Card(Suit.CLUBS, Nominal.ACE));

        assertEquals("[Десятка ♥️ (10), <закрытая карта>]",
                hand.toDisplayString(true));
    }

    @Test
    void hidingOnlyCardDoesNotAddHiddenCard() {
        Hand hand = new Hand();
        hand.addCard(new Card(Suit.HEARTS, Nominal.TEN));

        assertEquals("[Десятка ♥️ (10)]", hand.toDisplayString(true));
    }

    @Test
    void hiddenCardIsAlwaysSecondEvenWhenMoreCardsArePresent() {
        Hand hand = new Hand();
        hand.addCard(new Card(Suit.HEARTS, Nominal.TWO));
        hand.addCard(new Card(Suit.CLUBS, Nominal.THREE));
        hand.addCard(new Card(Suit.SPADES, Nominal.FOUR));

        assertEquals("[Двойка ♥️ (2), <закрытая карта>]",
                hand.toDisplayString(true));
    }

    @Test
    void allAcesCanBeReducedWhenHandIsStillOver21() {
        Hand hand = new Hand();
        hand.addCard(new Card(Suit.HEARTS, Nominal.ACE));
        hand.addCard(new Card(Suit.CLUBS, Nominal.ACE));
        hand.addCard(new Card(Suit.SPADES, Nominal.ACE));
        hand.addCard(new Card(Suit.DIAMONDS, Nominal.KING));

        assertEquals(13, hand.calculateScore());
        assertEquals(3, hand.getReducedAcesCount());
    }

    @Test
    void clearingHandRemovesAllCards() {
        Hand hand = new Hand();
        hand.addCard(new Card(Suit.HEARTS, Nominal.TWO));

        hand.clear();

        assertTrue(hand.getCards().isEmpty());
        assertEquals("[]", hand.toDisplayString(false));
    }

    @Test
    void participantStartsWithEmptyHandAndNoWins() {
        Player player = new Player("Игрок");

        assertEquals("Игрок", player.getName());
        assertEquals(0, player.getScore());
        assertFalse(player.isBusted());
        assertFalse(player.hasBlackjack());
        assertTrue(player.getHand().getCards().isEmpty());
    }

    @Test
    void twoCardsWorth21AreBlackjack() {
        Player player = new Player("Игрок");
        player.getHand().addCard(new Card(Suit.HEARTS, Nominal.ACE));
        player.getHand().addCard(new Card(Suit.SPADES, Nominal.KING));

        assertTrue(player.hasBlackjack());
        assertEquals(21, player.getScore());
    }

    @Test
    void twentyOneWithThreeCardsIsNotBlackjack() {
        Player player = new Player("Игрок");
        player.getHand().addCard(new Card(Suit.HEARTS, Nominal.SEVEN));
        player.getHand().addCard(new Card(Suit.SPADES, Nominal.SEVEN));
        player.getHand().addCard(new Card(Suit.CLUBS, Nominal.SEVEN));

        assertEquals(21, player.getScore());
        assertFalse(player.hasBlackjack());
        assertFalse(player.isBusted());
    }

    @Test
    void participantIsBustedAbove21() {
        Player player = new Player("Игрок");
        player.getHand().addCard(new Card(Suit.HEARTS, Nominal.KING));
        player.getHand().addCard(new Card(Suit.SPADES, Nominal.QUEEN));
        player.getHand().addCard(new Card(Suit.CLUBS, Nominal.TWO));

        assertEquals(22, player.getScore());
        assertTrue(player.isBusted());
    }

    @Test
    void participantCanStartNewRound() {
        Player player = new Player("Игрок");
        player.getHand().addCard(new Card(Suit.HEARTS, Nominal.TWO));

        player.resetHand();

        assertTrue(player.getHand().getCards().isEmpty());
    }

    @Test
    void dealerHitsBelowSeventeenAndStopsAtSeventeen() {
        Dealer dealer = new Dealer();
        dealer.getHand().addCard(new Card(Suit.HEARTS, Nominal.TEN));
        dealer.getHand().addCard(new Card(Suit.CLUBS, Nominal.SIX));
        assertTrue(dealer.shouldHit());

        dealer.getHand().addCard(new Card(Suit.SPADES, Nominal.ACE));
        assertEquals(17, dealer.getScore());
        assertFalse(dealer.shouldHit());
    }

    @Test
    void dealerKeepsTakingCardsWhenSoftScoreIsBelowSeventeen() {
        Dealer dealer = new Dealer();
        dealer.getHand().addCard(new Card(Suit.HEARTS, Nominal.ACE));
        dealer.getHand().addCard(new Card(Suit.CLUBS, Nominal.TWO));

        assertEquals(13, dealer.getScore());
        assertTrue(dealer.shouldHit());
    }

    @Test
    void dealerHasItsOwnName() {
        Dealer dealer = new Dealer();

        assertEquals("Дилер", dealer.getName());
    }

    @Test
    void gameScoreStartsAtZero() {
        GameScore score = new GameScore();

        assertEquals(0, score.getRoundNumber());
        assertEquals(0, score.getPlayerWins());
        assertEquals(0, score.getDealerWins());
        assertEquals("0:0", score.toString());
    }

    @Test
    void gameScoreTracksRoundsAndWins() {
        GameScore score = new GameScore();

        assertEquals(1, score.startRound());
        assertEquals(2, score.startRound());
        score.playerWon();
        score.dealerWon();
        score.dealerWon();

        assertEquals(2, score.getRoundNumber());
        assertEquals(1, score.getPlayerWins());
        assertEquals(2, score.getDealerWins());
        assertEquals("1:2", score.toString());
    }

    @Test
    void playerCanStopAfterAnInvalidInput() throws Exception {
        BlackjackGame game = newGame("wrong\n0\n");

        boolean busted = invoke(game, "handlePlayerTurn");

        assertFalse(busted);
    }

    @Test
    void deckCountInputRejectsInvalidValues() throws Exception {
        BlackjackGame game = newGame("text\n0\n3\n");

        int deckCount = invoke(game, "readDeckCount");

        assertEquals(3, deckCount);
    }

    @Test
    void emptyDeckInputCanBeRejectedBeforeAddingDeck() throws Exception {
        BlackjackGame game = newGame("0\n1\n");
        Deck deck = field(game, "deck", Deck.class);
        for (int i = 0; i < 52; i++) {
            deck.drawCard();
        }

        Card card = invoke(game, "drawCard");

        assertNotNull(card);
    }

    @Test
    void playerTurnEndsWhenInputIsClosed() throws Exception {
        BlackjackGame game = newGame("");

        boolean busted = invoke(game, "handlePlayerTurn");

        assertFalse(busted);
    }

    @Test
    void emptyDeckCanBeFilledThroughGameInput() throws Exception {
        BlackjackGame game = newGame("1\n");
        Deck deck = field(game, "deck", Deck.class);
        for (int i = 0; i < 52; i++) {
            deck.drawCard();
        }

        Card card = invoke(game, "drawCard");

        assertNotNull(card);
        assertFalse(deck.isEmpty());
    }

    @Test
    void dealerStopsWhenItsScoreReachesSeventeen() throws Exception {
        BlackjackGame game = newGame("");
        Dealer dealer = field(game, "dealer", Dealer.class);
        dealer.getHand().addCard(new Card(Suit.HEARTS, Nominal.TEN));
        dealer.getHand().addCard(new Card(Suit.CLUBS, Nominal.SEVEN));

        boolean busted = invoke(game, "handleDealerTurn");

        assertFalse(busted);
    }

    @Test
    void blackjackResolutionAwardsPlayerWin() throws Exception {
        BlackjackGame game = newGame("");
        Player player = field(game, "player", Player.class);
        final GameScore score = field(game, "gameScore", GameScore.class);
        player.getHand().addCard(new Card(Suit.HEARTS, Nominal.ACE));
        player.getHand().addCard(new Card(Suit.SPADES, Nominal.KING));

        invoke(game, "resolveBlackjack");

        assertEquals(1, score.getPlayerWins());
    }

    @Test
    void blackjackResolutionAwardsDealerWin() throws Exception {
        BlackjackGame game = newGame("");
        Dealer dealer = field(game, "dealer", Dealer.class);
        final GameScore score = field(game, "gameScore", GameScore.class);
        dealer.getHand().addCard(new Card(Suit.HEARTS, Nominal.ACE));
        dealer.getHand().addCard(new Card(Suit.SPADES, Nominal.KING));

        invoke(game, "resolveBlackjack");

        assertEquals(1, score.getDealerWins());
    }

    @Test
    void blackjackResolutionCanEndInTie() throws Exception {
        BlackjackGame game = newGame("");
        Player player = field(game, "player", Player.class);
        Dealer dealer = field(game, "dealer", Dealer.class);
        final GameScore score = field(game, "gameScore", GameScore.class);
        player.getHand().addCard(new Card(Suit.HEARTS, Nominal.ACE));
        player.getHand().addCard(new Card(Suit.SPADES, Nominal.KING));
        dealer.getHand().addCard(new Card(Suit.CLUBS, Nominal.ACE));
        dealer.getHand().addCard(new Card(Suit.DIAMONDS, Nominal.QUEEN));

        invoke(game, "resolveBlackjack");

        assertEquals(0, score.getPlayerWins());
        assertEquals(0, score.getDealerWins());
    }

    @Test
    void winnerIsSelectedForBothScoreOrdersAndTie() throws Exception {
        BlackjackGame game = newGame("");
        Player player = field(game, "player", Player.class);
        Dealer dealer = field(game, "dealer", Dealer.class);
        final GameScore score = field(game, "gameScore", GameScore.class);

        player.getHand().addCard(new Card(Suit.HEARTS, Nominal.TEN));
        dealer.getHand().addCard(new Card(Suit.CLUBS, Nominal.NINE));
        invoke(game, "determineWinner");
        assertEquals(1, score.getPlayerWins());

        player.resetHand();
        dealer.resetHand();
        player.getHand().addCard(new Card(Suit.HEARTS, Nominal.EIGHT));
        dealer.getHand().addCard(new Card(Suit.CLUBS, Nominal.NINE));
        invoke(game, "determineWinner");
        assertEquals(1, score.getDealerWins());

        player.resetHand();
        dealer.resetHand();
        player.getHand().addCard(new Card(Suit.HEARTS, Nominal.EIGHT));
        dealer.getHand().addCard(new Card(Suit.CLUBS, Nominal.EIGHT));
        invoke(game, "determineWinner");
        assertEquals(1, score.getPlayerWins());
        assertEquals(1, score.getDealerWins());
    }

    /**
     * Создаёт игру с подготовленным вводом.
     *
     * @param input строки, которые будут прочитаны игрой
     * @return новая игра
     */
    private static BlackjackGame newGame(String input) {
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        System.setOut(new PrintStream(new ByteArrayOutputStream()));
        return new BlackjackGame(new Deck(1), new Player("Игрок"),
                new Dealer(), new ConsoleInterface(), new GameScore());
    }

    /**
     * Получает закрытое поле игрового объекта.
     *
     * @param object объект, из которого нужно получить поле
     * @param name имя поля
     * @param type ожидаемый тип поля
     * @param <T> тип возвращаемого значения
     * @return значение поля
     * @throws Exception если поле не найдено или недоступно
     */
    @SuppressWarnings("unchecked")
    private static <T> T field(Object object, String name, Class<T> type)
            throws Exception {
        Field field = object.getClass().getDeclaredField(name);
        field.setAccessible(true);
        return (T) field.get(object);
    }

    /**
     * Вызывает закрытый метод игры.
     *
     * @param object объект, на котором вызывается метод
     * @param name имя метода без параметров
     * @param arguments аргументы вызова
     * @param <T> тип результата
     * @return результат работы метода
     * @throws Exception если метод недоступен
     */
    @SuppressWarnings("unchecked")
    private static <T> T invoke(Object object, String name, Object... arguments)
            throws Exception {
        Method method = object.getClass().getDeclaredMethod(name);
        method.setAccessible(true);
        return (T) method.invoke(object, arguments);
    }
}
