package ru.nsu.bogomolov.model;

/**
 * Достоинства карт и их базовые значения в игре.
 */
public enum Nominal {
    TWO("Двойка", 2),
    THREE("Тройка", 3),
    FOUR("Четверка", 4),
    FIVE("Пятерка", 5),
    SIX("Шестерка", 6),
    SEVEN("Семерка", 7),
    EIGHT("Восьмерка", 8),
    NINE("Девятка", 9),
    TEN("Десятка", 10),
    JACK("Валет", 10),
    QUEEN("Дама", 10),
    KING("Король", 10),
    ACE("Туз", 11);

    private final String name;
    private final int baseValue;


    Nominal(String name, int baseValue) {
        this.name = name;
        this.baseValue = baseValue;
    }

    /**
     * Возвращает подпись для пользователя.
     *
     * @return русское название достоинства
     */
    public String getName() {
        return name;
    }

    /**
     * Возвращает первоначальную стоимость карты.
     *
     * @return значение карты до особого правила для туза
     */
    public int getBaseValue() {
        return baseValue;
    }
}