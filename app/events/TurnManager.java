package events;

import akka.actor.ActorRef;
import structures.GameState;
import commands.BasicCommands;
import structures.basic.Card;

/**
 * Only human turn start/end orchestration is implemented in this sprint.
 */
public final class TurnManager {

    private TurnManager() {}

    // Mana Regeneration (increase max mana)
    public static void startHumanTurn(ActorRef out, GameState gs) {
        if (gs == null || gs.gameOver) return;

        if (gs.humanMaxMana < GameState.MAX_MANA_CAP) {
            gs.humanMaxMana += 1;
        }

        gs.humanCurrentMana = gs.humanMaxMana;

        // UI mana sync (BasicCommands reads from Player)
        gs.player1.setMana(gs.humanCurrentMana);
        BasicCommands.setPlayer1Mana(out, gs.player1);
    }

    public static void endHumanTurn(ActorRef out, GameState gs) {
        if (gs == null || gs.gameOver) return;

        // Clear multi-step UI context at end of turn
        gs.clearSelectionAndHighlights();

        // Integration points 
        invokeEndTurnDraw(out, gs);      // SC#26 (Chunying)
        invokeWinLossCheck(out, gs);     
        // Switch to AI (no AI logic in Sprint 2)
        gs.humanTurn = false;
        gs.aiTurnPending = true;
    }

    public static void invokeEndTurnDraw(ActorRef out, GameState gs) {
        if (gs == null || out == null) return;
        if (gs.gameOver) return;
        if (gs.player1 == null) return;

        // Only apply end-of-turn draw to the human player for now (human-only render)
        if (gs.player1.getHand().size() >= structures.basic.Hand.MAX_SIZE) return;

        // If deck is empty -> defeat
        Card drawn = gs.player1.getDeck().draw();
        if (drawn == null) {
            gs.gameOver = true;
            gs.winner = GameState.WINNER_AI;
            BasicCommands.addPlayer1Notification(out, "Game Over - Defeat (Deck empty)!", 10000);
            return;
        }

        boolean added = gs.player1.getHand().add(drawn);
        if (!added) return;

        int handPosition = gs.player1.getHand().size(); // 1..6
        BasicCommands.drawCard(out, drawn, handPosition, 0);
    }

    private static void invokeWinLossCheck(ActorRef out, GameState gs) {
        // SC#28/#29: Handle game-over logic when an Avatar is defeated
        
        // 1. Check if Human Avatar is defeated
        if (gs.humanAvatar != null) {
            Integer humanHp = gs.unitHp.get(gs.humanAvatar.getId());
            if (humanHp != null && humanHp <= 0) {
                System.out.println("GAME OVER: Human Avatar defeated. AI Wins!");
                BasicCommands.addPlayer1Notification(out, "Game Over - Defeat!", 10000);
            }
        }
        
        // 2. Check if AI Avatar is defeated
        if (gs.aiAvatar != null) {
            Integer aiHp = gs.unitHp.get(gs.aiAvatar.getId());
            if (aiHp != null && aiHp <= 0) {
                System.out.println("GAME OVER: AI Avatar defeated. Human Wins!");
                BasicCommands.addPlayer1Notification(out, "Game Over - Victory!", 10000);
            }
        }
    }

}