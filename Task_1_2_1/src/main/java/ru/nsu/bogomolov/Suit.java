package ru.nsu.bogomolov;

/**
 * Четыре масти стандартной колоды.
 */
public enum Suit {
    HEARTS("♥️"),
    DIAMONDS("♦️"),
    CLUBS("♣️"),
    SPADES("♠️");

    private final String symbol;

    Suit(String symbol) {
        this.symbol = symbol;
    }

    /**
     * Возвращает значок, используемый при печати карты.
     * @return символ масти
     */
    public String getSymbol() {
        return symbol;
    }
}