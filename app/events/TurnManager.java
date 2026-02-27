package events;

import akka.actor.ActorRef;
import structures.GameState;
import commands.BasicCommands;

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
        invokeWinLossCheck(out, gs);     // SC#28/#29 (Xinyu)

        // Switch to AI (no AI logic in Sprint 2)
        gs.humanTurn = false;
        gs.aiTurnPending = true;
    }

    private static void invokeEndTurnDraw(ActorRef out, GameState gs) {
        // SC#26 (Chunying)
    }

    private static void invokeWinLossCheck(ActorRef out, GameState gs) {
        // SC#28/#29 (Xinyu)
    }

}