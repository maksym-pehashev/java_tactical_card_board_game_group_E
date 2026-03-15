package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Tile;

/**
 * Indicates that the user has clicked an object on the game canvas, in this case a card.
 * The event returns the position in the player's hand the card resides within.
 * 
 * { 
 *   messageType = “cardClicked”
 *   position = <hand index position [1-6]>
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class CardClicked implements EventProcessor {

    @Override
    public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
        if (gameState == null || message == null) return;
        if (gameState.gameOver) return;
        if (!gameState.humanTurn) return;

        int handPosition = message.get("position").asInt();

        // ========== Business logic (to be implemented by Maksym) ==========
        // Should set gameState.selectedCard, gameState.selectedHandPosition, etc.
        // For now, we assume business logic runs before this point.
        // ===================================================================

        // ========== Yibo's UI rendering ==========
        // 1. Clear any existing tile highlights (from previous unit selection)
        if (gameState.highlightedTiles != null) {
            for (Tile tile : gameState.highlightedTiles) {
                BasicCommands.drawTile(out, tile, 0); // mode 0 = normal
                sleep(5);
            }
            gameState.highlightedTiles.clear();
        }

        // 2. Show BigCard for the selected card (command not yet available)
        // Card selectedCard = gameState.selectedCard;
        // if (selectedCard != null) {
        //     // TODO: Replace with actual command to show big card
        //     System.out.println("Show big card for: " + selectedCard.getCardname());
        //     // BasicCommands.showBigCard(out, selectedCard); // not exists
        // }
    }

    private void sleep(int ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}