package utils;

import structures.GameState;
import structures.basic.Unit;
import structures.basic.UnitAnimationType;
import commands.BasicCommands;
import akka.actor.ActorRef;

/**
 * Core class to handle unit death and board cleanup.
 */
public class DeathHandler {

    /**
     * Executes the death sequence for a unit.
     * @param deadUnit The unit that has died (HP <= 0)
     * @param gs The current game state
     * @param out ActorRef to send commands to the front-end
     */
    public static void handleDeath(Unit deadUnit, GameState gs, ActorRef out) {
        if (gs == null || out == null) return;
        if (deadUnit == null) return;

        int id = deadUnit.getId();

        BasicCommands.playUnitAnimation(out, deadUnit, UnitAnimationType.death);
        sleep(1200);

        BasicCommands.deleteUnit(out, deadUnit);
        sleep(200);

        // 1) Clear board occupancy
        if (gs.board != null && deadUnit.getPosition() != null) {
            int x = deadUnit.getPosition().getTilex();
            int y = deadUnit.getPosition().getTiley();
            if (gs.board.inBounds(x, y)) {
                // Safety: clear only if this unit is still registered there
                if (gs.board.getUnitAt(x, y) == deadUnit) {
                    gs.board.clearUnitAt(x, y);
                } else {
                    // Still clear if occupied by the same id - keep simple for now
                    gs.board.clearUnitAt(x, y);
                }
            }
        }

        // 2) Remove from backend lists
        gs.humanUnits.remove(deadUnit);
        gs.aiUnits.remove(deadUnit);

        // 3) Remove from authoritative stat maps
        gs.unitHp.remove(id);
        gs.unitAtk.remove(id);

        // 4) Remove from authoritative flag maps (avoid “ghost ids”)
        gs.canMove.remove(id);
        gs.canAttack.remove(id);
        gs.exhausted.remove(id);
        gs.summoningSickness.remove(id);

        // 5) Clear selection/highlights if needed
        if (gs.selectedUnit != null && gs.selectedUnit.getId() == id) {
            gs.clearSelectionAndHighlights();
        }

        // --- Avatar death check (Game Over condition) ---
        if (gs.humanAvatar != null && id == gs.humanAvatar.getId()) {
            gs.gameOver = true;
            gs.winner = GameState.WINNER_AI;
            System.out.println("GAME OVER: Human Avatar defeated. AI Wins!");
            BasicCommands.addPlayer1Notification(out, "Game Over - Defeat!", 10000);
        } else if (gs.aiAvatar != null && id == gs.aiAvatar.getId()) {
            gs.gameOver = true;
            gs.winner = GameState.WINNER_HUMAN;
            System.out.println("GAME OVER: AI Avatar defeated. Human Wins!");
            BasicCommands.addPlayer1Notification(out, "Game Over - Victory!", 10000);
        }
    }

    // Utility method to keep code clean
    private static void sleep(int ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { e.printStackTrace(); }
    }
    
    /**
     * SC#28/#29: Checks if a player has lost due to an empty deck during a required draw.
     * @param isHuman true if checking the human player, false for AI
     * @param out ActorRef to send commands to the front-end
     */
    public static void checkDeckEmptyDefeat(boolean isHuman, ActorRef out) {
        if (isHuman) {
            System.out.println("GAME OVER: Human player's deck is empty. AI Wins!");
            BasicCommands.addPlayer1Notification(out, "Game Over - Defeat! (Out of cards)", 10000);
        } else {
            System.out.println("GAME OVER: AI player's deck is empty. Human Wins!");
            BasicCommands.addPlayer1Notification(out, "Game Over - Victory! (AI out of cards)", 10000);
        }
    }
}