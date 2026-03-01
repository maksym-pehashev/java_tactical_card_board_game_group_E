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
        invokeEndTurnDraw(out, gs);
        invokeWinLossCheck(out, gs);     // SC#28/#29 (Xinyu)

        // Switch to AI (no AI logic in Sprint 2)
        gs.humanTurn = false;
        gs.aiTurnPending = true;
    }

    public static void invokeEndTurnDraw(ActorRef out, GameState gs) {
        if (gs == null) return;
        if (gs.gameOver) return;
        if (gs.player1 == null) return;
        //Only apply end-of-turn draw to the human player for now (human-only render)
        if (gs.player1.getHand().size() >= structures.basic.Hand.MAX_SIZE) return;
        //if deck is empty, defeat
        Card drawn = gs.player1.getDeck().draw();
        if (drawn == null) {
            gs.gameOver = true;
            gs.winner = "player2";
            BasicCommands.addPlayer1Notification(out, "Sorry, your deck is empty. You lose the game.", 2);
            return;
        }

        // Add to hand (should succeed because we checked size < 6)
        boolean added = gs.player1.getHand().add(drawn);
        if (!added) return;
        // Render the card into the next free slot (1..6)
        int handPosition = gs.player1.getHand().size();
        BasicCommands.drawCard(out, drawn, handPosition, 0);
    }

    private static void invokeWinLossCheck(ActorRef out, GameState gs) {
        // SC#28/#29 (Xinyu)
    }

}