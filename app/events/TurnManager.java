package events;

import akka.actor.ActorRef;
import structures.GameState;

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

        // UI update will be wired once we plug in your existing BasicCommands call
        gs.humanCurrentMana = gs.humanMaxMana;
    }

    public static void endHumanTurn(ActorRef out, GameState gs) {

    }


}