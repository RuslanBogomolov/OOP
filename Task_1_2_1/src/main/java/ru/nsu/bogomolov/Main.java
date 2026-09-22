package ru.nsu.bogomolov;

import ru.nsu.bogomolov.game.BlackjackGame;

/**
 * Точка входа в консольную игру.
 */
public final class Main {
    private Main() {
    }

    /**
     * Запускает игру.
     *
     * @param args аргументы командной строки, не используются
     */
    public static void main(String[] args) {
        new BlackjackGame().start();
    }
}