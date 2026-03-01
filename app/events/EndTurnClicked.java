package events;

import com.fasterxml.jackson.databind.JsonNode;
import akka.actor.ActorRef;
import structures.GameState;

/**
 * Indicates that the user has clicked an object on the game canvas, in this case
 * the end-turn button.
 * 
 * { 
 *   messageType = “endTurnClicked”
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class EndTurnClicked implements EventProcessor{
	@Override
 	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		// Only allow end turn during the human turn
		if (gameState == null || gameState.gameOver) return;
    	if (!gameState.humanTurn) return;
		System.out.println("[EndTurnClicked] Human ended turn -> handing off to TurnManager");
		TurnManager.endHumanTurn(out, gameState);
	}
}

