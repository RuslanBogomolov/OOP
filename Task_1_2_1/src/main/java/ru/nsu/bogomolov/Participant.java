package ru.nsu.bogomolov;

/**
 * Общая логика игрока и дилера.
 */
abstract class Participant {
    private final String name;
    protected final Hand hand = new Hand();

    /**
     * @param name имя участника
     */
    public Participant(String name) {
        this.name = name;
    }

    /**
     * @return имя участника
     */
    public String getName() { return name; }
    /**
     * @return рука участника
     */
    public Hand getHand() { return hand; }
    /**
     * Возвращает текущую сумму очков.
     * @return текущая сумма очков
     */
    public int getScore() {
        return hand.calculateScore();
    }

    /**
     * Проверяет, превысил ли участник допустимую сумму.
     * @return true, если сумма очков больше 21
     */
    public boolean isBusted() {
        return getScore() > 21;
    }

    /**
     * Проверяет наличие блэкджека на первых двух картах.
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

class Player extends Participant {
    /**
     * @param name имя игрока
     */
    public Player(String name) {
        super(name);
    }
}

class Dealer extends Participant {
    /**
     * Создаёт дилера с фиксированным именем.
     */
    public Dealer() {
        super("Дилер");
    }

    /**
     * Дилер берёт карту, пока его сумма меньше 17.
     * @return true, если дилеру нужно взять карту
     */
    public boolean shouldHit() {
        return getScore() < 17;
    }
}
