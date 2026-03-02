package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Position;
import structures.basic.Tile;
import structures.basic.Unit;

public class TileClicked implements EventProcessor {

	private UnitClicked unitClickedHelper = new UnitClicked();

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		System.out.println("=== TileClicked event received ===");

		int tilex = message.get("tilex").asInt();
		int tiley = message.get("tiley").asInt();
		System.out.println("Original tilex=" + tilex + ", tiley=" + tiley); // 打印原始坐标

		int x = tilex - 1;
		int y = tiley - 1;
		System.out.println("Clicked tile: (" + x + "," + y + ")");

		Tile clickedTile = gameState.board.getTile(x, y);
		if (clickedTile == null) {
			System.out.println("Tile not found");
			return;
		}

		Unit unitOnTile = gameState.board.getUnitAt(x, y);

		// 情况1：点击的单位且没有选中任何单位
		if (unitOnTile != null && gameState.selectedUnit == null) {
			unitClickedHelper.selectUnit(out, gameState, unitOnTile);
			return;
		}

		// 情况2：点击的单位是当前选中的单位
		if (unitOnTile != null && gameState.selectedUnit != null && unitOnTile == gameState.selectedUnit) {
			System.out.println("Deselecting current unit");
			clearHighlights(out, gameState);
			return;
		}

		// 情况3：点击的单位不是当前选中的单位
		if (unitOnTile != null && gameState.selectedUnit != null && unitOnTile != gameState.selectedUnit) {
			System.out.println("Clicked another unit, ignoring (keep current selection)");
			return;
		}

		// 情况4：点击空格子，且当前有选中的单位
		if (unitOnTile == null && gameState.selectedUnit != null) {
			handleMove(out, gameState, clickedTile);
			return;
		}

		// 情况5：点击空格子，无选中单位
		System.out.println("Clicked empty tile, no unit selected, ignoring");
	}

	private void handleMove(ActorRef out, GameState gs, Tile targetTile) {
		if (gs.highlightedTiles == null || !gs.highlightedTiles.contains(targetTile)) {
			System.out.println("Clicked tile not highlighted, cancelling selection");
			clearHighlights(out, gs);
			return;
		}

		Unit unit = gs.selectedUnit;
		Position oldPos = unit.getPosition();
		int oldX = oldPos.getTilex();
		int oldY = oldPos.getTiley();
		int newX = targetTile.getTilex();
		int newY = targetTile.getTiley();

		System.out.println("Moving unit from (" + oldX + "," + oldY + ") to (" + newX + "," + newY + ")");

		unit.setPositionByTile(targetTile);
		gs.board.setUnitAt(oldX, oldY, null);
		gs.board.setUnitAt(newX, newY, unit);
		gs.canMove.put(unit.getId(), false);
		System.out.println("Unit canMove set to false");

		clearHighlights(out, gs);
		BasicCommands.moveUnitToTile(out, unit, targetTile);
		System.out.println("Move command sent");
	}

	private void clearHighlights(ActorRef out, GameState gs) {
		if (gs.highlightedTiles != null) {
			for (Tile tile : gs.highlightedTiles) {
				BasicCommands.drawTile(out, tile, 0);
				sleep(5);
			}
			gs.highlightedTiles.clear();
		}
		gs.selectedUnit = null;
	}

	private void sleep(int ms) {
		try { Thread.sleep(ms); } catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}