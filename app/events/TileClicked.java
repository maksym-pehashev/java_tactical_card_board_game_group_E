package events;


import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Tile;
import structures.basic.Unit;
import structures.basic.UnitAnimationType;
import utils.CombatResolver;

/**
 * Indicates that the user has clicked an object on the game canvas, in this case a tile.
 * The event returns the x (horizontal) and y (vertical) indices of the tile that was
 * clicked. Tile indices start at 1.
 * 
 * { 
 *   messageType = “tileClicked”
 *   tilex = <x index of the tile>
 *   tiley = <y index of the tile>
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class TileClicked implements EventProcessor{

	@Override
	public void processEvent(ActorRef out, GameState gs, JsonNode message) {

		if (gs == null || message == null) return;
		if (gs.gameOver) return;
		if (!gs.humanTurn) return;

		// UI sends 1-indexed, board is 0-indexed
		int tilex = message.get("tilex").asInt();
		int tiley = message.get("tiley").asInt();

		if (gs.board == null || !gs.board.inBounds(tilex, tiley)) return;

		Tile clickedTile = gs.board.getTile(tilex, tiley);
		if (clickedTile == null) return;

		Unit occupant = gs.board.getUnitAt(tilex, tiley);
		
		// If friendly unit on clicked tile -> delegate selection/highlights to UnitClicked
		if (occupant != null && Rules.isHumanUnit(gs, occupant)) {
			UnitClicked.onUnitSelected(out, gs, occupant);
			return;
		}

		// 2) Attack enemy unit (if selectedUnit exists)
		if (occupant != null && gs.selectedUnit != null) {
			Unit attacker = gs.selectedUnit;
			Unit defender = occupant;

			if (Rules.isHumanUnit(gs, defender)) return; // no friendly fire

			Rules.ValidationResult ar = Rules.validateAttack(gs, attacker, defender);
			if (!ar.ok) return;

			CombatResolver.attack(attacker.getId(), defender.getId(), gs, out);

			clearHighlightsUI(out, gs);
			return;
		}

		// 3) Move to empty tile (if selectedUnit exists)
		if (occupant == null && gs.selectedUnit != null) {

			Unit unit = gs.selectedUnit;

			Rules.ValidationResult vr = Rules.validateMove(gs, unit, clickedTile);
			if (!vr.ok) return;

			int fromX = unit.getPosition().getTilex();
			int fromY = unit.getPosition().getTiley();

			// Update board occupancy (authoritative)
			gs.board.clearUnitAt(fromX, fromY);
			gs.board.setUnitAt(tilex, tiley, unit);

			// Update unit position (authoritative)
			unit.setPositionByTile(clickedTile);

			// UI: play move + animate movement
			BasicCommands.playUnitAnimation(out, unit, UnitAnimationType.move);
			BasicCommands.moveUnitToTile(out, unit, clickedTile);

			// flags
			gs.onMoveDone(unit.getId());

			clearHighlightsUI(out, gs);
			return;
		}
	}

	private void clearHighlightsUI(ActorRef out, GameState gs) {
		for (Tile t : gs.highlightedTiles) {
			BasicCommands.drawTile(out, t, 0);  // highlight OFF
		}
		gs.clearSelectionAndHighlights();
	}

}
