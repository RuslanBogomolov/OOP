package ru.nsu.bogomolov.model;

/**
 * Общая логика игрока и дилера.
 */
public abstract class Participant {
    private final String name;
    protected final Hand hand = new Hand();

    /**
     * Создаёт участника игры.
     *
     * @param name имя участника
     */
    public Participant(String name) {
        this.name = name;
    }

    /**
     * Возвращает имя участника.
     *
     * @return имя участника
     */
    public String getName() {
        return name;
    }

    /**
     * Возвращает руку участника.
     *
     * @return рука участника
     */
    public Hand getHand() {
        return hand;
    }

    /**
     * Возвращает текущую сумму очков.
     *
     * @return текущая сумма очков
     */
    public int getScore() {
        return hand.calculateScore();
    }

    /**
     * Проверяет, превысил ли участник допустимую сумму.
     *
     * @return true, если сумма очков больше 21
     */
    public boolean isBusted() {
        return getScore() > 21;
    }

    /**
     * Проверяет наличие блэкджека на первых двух картах.
     * Проверяет наличие блэкджека на первых двух картах.
     *
     * @return true, если на руке ровно две карты на 21 очко
     */
    public boolean hasBlackjack() {
        return hand.getCards().size() == 2 && getScore() == 21;
    }

    /**
     * Очищает руку перед началом нового раунда.
     */
    public void resetHand() {
        hand.clear();
    }
}
