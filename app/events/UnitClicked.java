package events;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Position;
import structures.basic.Tile;
import structures.basic.Unit;

public class UnitClicked implements EventProcessor {

    @Override
    public void processEvent(ActorRef out, GameState gs, JsonNode message) {
        // 此事件不会被前端触发，保留以符合框架要求
        System.out.println("UnitClicked.processEvent called (should not happen)");
    }

    /**
     * 由 TileClicked 调用，执行单位选择和高亮逻辑。
     */
    public void selectUnit(ActorRef out, GameState gs, Unit unit) {
        System.out.println("UnitClicked.selectUnit: unit id " + unit.getId());

        // 检查回合和游戏是否结束
        if (!gs.humanTurn || gs.gameOver) {
            System.out.println("Not human turn or game over");
            return;
        }

        // 检查是否为我方单位（简化比较）
        if (!isHumanUnit(unit, gs)) {
            System.out.println("Unit is not human");
            return;
        }

        // 检查单位是否可以移动
        if (!gs.canUnitMove(unit.getId())) {
            System.out.println("Unit cannot move");
            return;
        }

        // 清除之前的高亮和选择
        clearHighlights(out, gs);

        // 计算可移动格子（简化版：所有相邻空格，后续可替换为 Rules.getValidMoveTiles）
        List<Tile> validTiles = calculateValidMoveTilesSimple(unit, gs);
        System.out.println("Valid tiles count: " + validTiles.size());

        // 保存到 GameState
        gs.selectedUnit = unit;
        gs.highlightedTiles = validTiles;

        // 高亮格子（模式 1 为白色）
        for (Tile tile : validTiles) {
            BasicCommands.drawTile(out, tile, 1);
            sleep(10);
        }
    }

    private boolean isHumanUnit(Unit unit, GameState gs) {
        if (unit == gs.humanAvatar) return true;
        for (Unit u : gs.humanUnits) {
            if (u == unit) return true;
        }
        return false;
    }

    private List<Tile> calculateValidMoveTilesSimple(Unit unit, GameState gs) {
        List<Tile> result = new ArrayList<>();
        Position pos = unit.getPosition();
        int x = pos.getTilex();
        int y = pos.getTiley();

        int[][] dirs = {
                {0,1}, {0,-1}, {1,0}, {-1,0},
                {1,1}, {1,-1}, {-1,1}, {-1,-1}
        };

        for (int[] d : dirs) {
            int nx = x + d[0];
            int ny = y + d[1];
            if (isValidTile(gs, nx, ny)) {
                result.add(gs.board.getTile(nx, ny));
            }
        }
        return result;
    }

    private boolean isValidTile(GameState gs, int x, int y) {
        if (x < 0 || x >= 9 || y < 0 || y >= 5) return false;
        return gs.board.getUnitAt(x, y) == null;
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