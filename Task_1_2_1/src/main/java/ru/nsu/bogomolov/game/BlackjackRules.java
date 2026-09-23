package ru.nsu.bogomolov.game;

import ru.nsu.bogomolov.model.Dealer;
import ru.nsu.bogomolov.model.Player;

/**
 * Правила определения результата раунда.
 */
public class BlackjackRules {
    /**
     * Сравнивает очки игрока и дилера.
     *
     * @param player игрок
     * @param dealer дилер
     * @return результат раунда
     */
    public RoundResult determineWinner(Player player, Dealer dealer) {
        int playerScore = player.getScore();
        int dealerScore = dealer.getScore();

        if (playerScore > dealerScore) {
            return RoundResult.PLAYER_WIN;
        }
        if (dealerScore > playerScore) {
            return RoundResult.DEALER_WIN;
        }
        return RoundResult.DRAW;
    }
}
