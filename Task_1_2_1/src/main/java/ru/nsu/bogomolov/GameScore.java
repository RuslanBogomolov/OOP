package ru.nsu.bogomolov;

/**
 * Хранит результаты сыгранных раундов.
 */
class GameScore {
    private int roundNumber;
    private int playerWins;
    private int dealerWins;

    /**
     * Переходит к следующему раунду.
     * @return номер начавшегося раунда
     */
    public int startRound() {
        return ++roundNumber;
    }

    /**
     * Учитывает победу игрока.
     */
    public void playerWon() {
        playerWins++;
    }

    /**
     * Учитывает победу дилера.
     */
    public void dealerWon() {
        dealerWins++;
    }

    /**
     * @return количество сыгранных раундов
     */
    public int getRoundNumber() {
        return roundNumber;
    }

    /**
     * @return количество побед игрока
     */
    public int getPlayerWins() {
        return playerWins;
    }

    /**
     * @return количество побед дилера
     */
    public int getDealerWins() {
        return dealerWins;
    }

    /**
     * @return счёт в формате «победы игрока:победы дилера»
     */
    @Override
    public String toString() {
        return playerWins + ":" + dealerWins;
    }
}
