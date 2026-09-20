package ru.nsu.bogomolov;

/**
 * Дилер в блэкджек.
 */
class Dealer extends Participant {
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
}
