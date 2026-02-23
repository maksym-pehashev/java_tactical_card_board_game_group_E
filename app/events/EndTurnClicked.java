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
		// only allow end-turn from the human side in this placeholder implementation
		if (!gameState.humanTurn) {System.out.println("[EndTurnClicked] Human ended trun -> switching to AI(placeholder)");
		// clear any multi-step interaction state so it doesn't carry across turns
		gameState.clearSelectionAndHighlights();
		// switch to AI turn and marked AI to be processed once on next heartbeat tick
		gameState.humanTurn = false;
		gameState.aiTurnPending = true;
		}
	}

}
