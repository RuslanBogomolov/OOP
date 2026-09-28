package ru.nsu.bogomolov.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Карты, собранные одним участником текущего раунда.
 */
public class Hand {
    private final List<Card> cards = new ArrayList<>();

    /**
     * Добавляет карту в руку.
     *
     * @param card карта для добавления
     */
    public void addCard(Card card) {
        cards.add(card);
    }

    /**
     * Удаляет все карты из руки.
     */
    public void clear() {
        cards.clear();
    }

    /**
     * Возвращает карты в порядке их получения.
     *
     * @return список карт в порядке их получения
     */
    public List<Card> getCards() {
        return List.copyOf(cards);
    }

    /**
     * Возвращает количество карт в руке.
     *
     * @return количество карт
     */
    public int size() {
        return cards.size();
    }

    /**
     * Возвращает карту по её позиции в руке.
     *
     * @param index индекс карты
     * @return карта в указанной позиции
     */
    public Card getCard(int index) {
        return cards.get(index);
    }

    /**
     * Считает очки с учётом значения туза 1 или 11.
     *
     * @return итоговое количество очков
     */
    public int calculateScore() {
        int total = 0;
        int aceCount = 0;

        for (Card card : cards) {
            total += card.getNominal().getBaseValue();
            if (card.getNominal() == Nominal.ACE) {
                aceCount++;
            }
        }

        while (total > 21 && aceCount > 0) {
            total -= 10;
            aceCount--;
        }

        return total;
    }

    /**
     * Определяет количество тузов со значением 1.
     *
     * @return количество тузов со значением 1
     */
    public int getReducedAcesCount() {
        return getReducedAcesCount(cards.size());
    }

    /**
     * Определяет количество уменьшенных тузов среди первых карт руки.
     *
     * @param cardCount количество учитываемых карт
     * @return количество тузов со значением 1
     */
    public int getReducedAcesCount(int cardCount) {
        if (cardCount < 0 || cardCount > cards.size()) {
            throw new IllegalArgumentException(
                    "Количество карт должно быть от 0 до размера руки");
        }

        int total = 0;
        int aceCount = 0;

        for (int i = 0; i < cardCount; i++) {
            Card card = cards.get(i);
            total += card.getNominal().getBaseValue();
            if (card.getNominal() == Nominal.ACE) {
                aceCount++;
            }
        }

        int reducedAces = 0;
        while (total > 21 && aceCount > 0) {
            total -= 10;
            aceCount--;
            reducedAces++;
        }
        return reducedAces;
    }

}