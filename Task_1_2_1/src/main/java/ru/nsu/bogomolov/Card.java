package ru.nsu.bogomolov;

/**
 * Одна игральная карта с мастью и достоинством.
 */
public class Card {
    private final Suit suit;
    private final Nominal nominal;

    /**
     * Создаёт карту.
     *
     * @param suit масть карты
     * @param nominal достоинство карты
     */
    public Card(Suit suit, Nominal nominal) {
        this.suit = suit;
        this.nominal = nominal;
    }

    /**
     * Возвращает масть, например пики или червы.
     *
     * @return масть карты
     */
    public Suit getSuit() {
        return suit;
    }

    /**
     * Возвращает достоинство карты.
     *
     * @return достоинство карты
     */
    public Nominal getNominal() {
        return nominal;
    }

    /**
     * @return название карты и символ её масти
     */
    @Override
    public String toString() {
        return nominal.getName() + " " + suit.getSymbol();
    }
}