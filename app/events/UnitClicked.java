package events;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Tile;
import structures.basic.Unit;

public class UnitClicked {

    public static void onUnitSelected(ActorRef out, GameState gs, Unit unit) {

        if (gs == null || unit == null) return;
        if (gs.gameOver) return;
        if (!gs.humanTurn) return;

        // only friendly selectable
        if (!Rules.isHumanUnit(gs, unit)) return;

        // clear old highlights in UI
        for (Tile t : gs.highlightedTiles) {
            BasicCommands.drawTile(out, t, 0);
        }
        gs.clearSelectionAndHighlights();

        gs.selectedUnit = unit;

        // move highlight
        for (Tile t : Rules.getValidMoveTiles(gs, unit.getId())) {
            BasicCommands.drawTile(out, t, 1);
            gs.highlightedTiles.add(t);
        }

        // attack highlight
        for (Unit enemy : Rules.getValidAttackTargets(gs, unit.getId())) {
            Tile enemyTile = gs.board.getTile(enemy.getPosition().getTilex(), enemy.getPosition().getTiley());
            if (enemyTile != null) {
                BasicCommands.drawTile(out, enemyTile, 2);
                gs.highlightedTiles.add(enemyTile);
            }
        }
    }
}