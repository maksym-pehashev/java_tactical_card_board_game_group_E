package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import structures.GameState;

/**
 * In the user’s browser, the game is running in an infinite loop, where there is around a 1 second delay 
 * between each loop. Its during each loop that the UI acts on the commands that have been sent to it. A 
 * heartbeat event is fired at the end of each loop iteration. As with all events this is received by the Game 
 * Actor, which you can use to trigger game logic.
 * 
 * { 
 *   String messageType = “heartbeat”
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class Heartbeat implements EventProcessor{

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		// AI placeholder: if it is AI's turn and pending is true, do nothing and end AI turn directly.
		if (!gameState.humanTurn && gameState.aiTurnPending){System.out.println("[Heartbeat] AI placeholder executing -> ending AI return directly");
		// clear pending first to prevent repeating every heartbeat
		gameState.aiTurnPending = false;
		// clear any stale interaction state before switching back
		gameState.clearSelectionAndHighlights();
		// Switch back to human
		gameState.humanTurn = true;
		TurnManager.startHumanTurn(out, gameState);
		gameState.resetFlagsAtTurnStart();
		System.out.println("[Heartbeat] Switched back to human turn");
		}
	}

}
