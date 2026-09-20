package ru.nsu.bogomolov;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Колода карт, из которой игроки берут карты.
 */
class Deck {
    private final List<Card> cards = new ArrayList<>();
    private int deckCount;

    /**
     * Создаёт и перемешивает стандартные колоды.
     *
     * @param deckCount количество колод
     */
    public Deck(int deckCount) {
        this.deckCount = deckCount;
        reset();
    }

    /**
     * Собирает колоду заново и перемешивает карты.
     */
    public void reset() {
        cards.clear();
        for (int d = 0; d < deckCount; d++) {
            for (Suit suit : Suit.values()) {
                for (Nominal nominal : Nominal.values()) {
                    cards.add(new Card(suit, nominal));
                }
            }
        }
        shuffle();
    }

    /**
     * Перемешивает карты в текущей колоде.
     */
    public void shuffle() {
        Collections.shuffle(cards);
    }

    /**
     * Добавляет колоду и перемешивает карты.
     */
    public void addDeck() {
        deckCount++;
        for (Suit suit : Suit.values()) {
            for (Nominal nominal : Nominal.values()) {
                cards.add(new Card(suit, nominal));
            }
        }
        shuffle();
    }

    /**
     * Проверяет, остались ли карты в колоде.
     *
     * @return true, если в колоде не осталось карт
     */
    public boolean isEmpty() {
        return cards.isEmpty();
    }

    /**
     * Выдаёт верхнюю карту.
     *
     * @return вынутая карта
     * @throws IllegalStateException если в колоде не осталось карт
     */
    public Card drawCard() {
        if (cards.isEmpty()) {
            throw new IllegalStateException("В колоде не осталось карт");
        }
        return cards.remove(cards.size() - 1);
    }
}
