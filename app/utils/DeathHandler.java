package utils;

import structures.GameState;
import structures.basic.Unit;
import structures.basic.UnitAnimationType;
import commands.BasicCommands;
import akka.actor.ActorRef;

/**
 * Core class to handle unit death and board cleanup (SC#24).
 */
public class DeathHandler {

    /**
     * Executes the death sequence for a unit.
     * @param deadUnit The unit that has died (HP <= 0)
     * @param gs The current game state
     * @param out ActorRef to send commands to the front-end
     */
    public static void handleDeath(Unit deadUnit, GameState gs, ActorRef out) {
        if (deadUnit == null) return;

        System.out.println("Unit " + deadUnit.getId() + " has fallen! Initiating cleanup...");

        // 1. Play the death animation on the front-end
        BasicCommands.playUnitAnimation(out, deadUnit, UnitAnimationType.death);
        sleep(1200); // Give it a slightly longer pause to let the animation play fully

        // 2. Remove the unit visually from the board
        BasicCommands.deleteUnit(out, deadUnit);
        sleep(200);

        // SC#24: Clear selection if the dying unit is currently selected
        if (gs.selectedUnit != null && gs.selectedUnit.getId() == deadUnit.getId()) {
            gs.selectedUnit = null; 
            System.out.println("Selection cleared because the selected unit died.");
        }

        // 3. Remove the unit from the back-end GameState lists
        gs.humanUnits.remove(deadUnit);
        gs.aiUnits.remove(deadUnit);
        
        // Remove from stat maps to prevent ghost data
        gs.unitHp.remove(deadUnit.getId());
        gs.unitAtk.remove(deadUnit.getId());

        System.out.println("Unit " + deadUnit.getId() + " successfully removed from the game.");
        
        // --- Avatar death check (Game Over condition SC#28/#29) ---
        if (gs.humanAvatar != null && deadUnit.getId() == gs.humanAvatar.getId()) {
            System.out.println("GAME OVER: Human Avatar defeated. AI Wins!");
            BasicCommands.addPlayer1Notification(out, "Game Over - Defeat!", 10000);
        } else if (gs.aiAvatar != null && deadUnit.getId() == gs.aiAvatar.getId()) {
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