package ru.nsu.bogomolov;

import java.util.ArrayList;
import java.util.List;

/**
 * Карты, собранные одним участником текущего раунда.
 */
class Hand {
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
        return cards;
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
        int total = 0;
        int aceCount = 0;

        for (Card card : cards) {
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

    /**
     * Формирует строку для вывода руки в консоль.
     *
     * @param hideSecondCard нужно ли скрыть вторую карту
     * @return карты и сумму, если карты открыты
     */
    public String toDisplayString(boolean hideSecondCard) {
        if (cards.isEmpty()) {
            return "[]";
        }

        StringBuilder sb = new StringBuilder("[");
        int reducedAcesToApply = getReducedAcesCount();

        for (int i = 0; i < cards.size(); i++) {
            if (i == 1 && hideSecondCard) {
                sb.append(", ");
                sb.append("<закрытая карта>");
                break;
            }

            Card card = cards.get(i);
            int value = card.getNominal().getBaseValue();

            if (card.getNominal() == Nominal.ACE) {
                if (reducedAcesToApply > 0) {
                    value = 1;
                    reducedAcesToApply--;
                } else {
                    value = 11;
                }
            }

            sb.append(card).append(" (").append(value).append(")");

            if (i < cards.size() - 1 && !(i == 0 && hideSecondCard)) {
                sb.append(", ");
            }
        }
        sb.append("]");

        if (!hideSecondCard) {
            sb.append(" > ").append(calculateScore());
        }

        return sb.toString();
    }
}