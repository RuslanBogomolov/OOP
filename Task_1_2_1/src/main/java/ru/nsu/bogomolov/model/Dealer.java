package ru.nsu.bogomolov.model;

/**
 * Дилер в блэкджек.
 */
public class Dealer extends Participant {
    /**
     * Создаёт дилера с фиксированным именем.
     */
    public Dealer() {
        super("Дилер");
    }

    /**
     * Проверяет, должен ли дилер взять карту.
     *
     * @return true, если дилеру нужно взять карту
     */
    public boolean shouldHit() {
        return getScore() < 17;
    }

    /**
     * Возвращает закрытую карту дилера.
     *
     * @return вторая карта дилера
     */
    public Card getHiddenCard() {
        return getHand().getCards().get(1);
    }
}
