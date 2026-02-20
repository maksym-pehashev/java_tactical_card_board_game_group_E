package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Board;
import structures.basic.Player;
import structures.basic.Tile;
import structures.basic.Unit;
import utils.BasicObjectBuilders;

public class Initalize implements EventProcessor {
    private static final int BOARD_WIDTH = 9;
    private static final int BOARD_HEIGHT = 5;

    // Docs: [2,3] in 1-indexed => (1,2) 0-indexed
    private static final int HUMAN_AVATAR_X = 1;
    private static final int HUMAN_AVATAR_Y = 2;

    // Mirrored
    private static final int AI_AVATAR_X = 7;
    private static final int AI_AVATAR_Y = 2;

    private static final int HUMAN_AVATAR_ID = 0;
    private static final int AI_AVATAR_ID = 1;

    @Override
    public void processEvent(ActorRef out, GameState gs, JsonNode message) {
        if (gs == null) return;

        // Prevent double init
        if (gs.gameInitalised) return;

        // Core state
        gs.gameOver = false;
        gs.winner = null;
        gs.humanTurn = true;
        gs.turnNumber = 1;

        gs.player1 = new Player(20, 0);
        gs.player2 = new Player(20, 0);

        gs.humanMaxMana = 2;
        gs.aiMaxMana = 2;
        gs.humanCurrentMana = 2;
        gs.aiCurrentMana = 2;

        gs.player1.setMana(gs.humanCurrentMana);
        gs.player2.setMana(gs.aiCurrentMana);

        gs.clearSelectionAndHighlights();

        // Board model + basic UI draw
        gs.board = new Board(BOARD_WIDTH, BOARD_HEIGHT);
        for (int x = 0; x < BOARD_WIDTH; x++) {
            for (int y = 0; y < BOARD_HEIGHT; y++) {
                Tile tile = BasicObjectBuilders.loadTile(x, y);
                gs.board.setTile(x, y, tile);
                BasicCommands.drawTile(out, tile, 0);
            }
        }

        // Spawn avatars
        gs.humanAvatar = spawnAvatar(out, gs, HUMAN_AVATAR_ID, HUMAN_AVATAR_X, HUMAN_AVATAR_Y,
                "conf/gameconfs/avatars/avatar1.json", 20, 2);

        gs.aiAvatar = spawnAvatar(out, gs, AI_AVATAR_ID, AI_AVATAR_X, AI_AVATAR_Y,
                "conf/gameconfs/avatars/avatar2.json", 20, 2);

        // Mark initialised 
        gs.gameInitalised = true;
    }

    private Unit spawnAvatar(ActorRef out, GameState gs, int id, int x, int y, String conf, int hp, int atk) {
        Unit avatar = BasicObjectBuilders.loadUnit(conf, id, Unit.class);
        Tile tile = gs.board.getTile(x, y);

        avatar.setPositionByTile(tile);
        gs.board.setUnitAt(x, y, avatar);

        // Basic visual draw (minimal)
        BasicCommands.drawUnit(out, avatar, tile);
        sleep(200);
        BasicCommands.setUnitHealth(out, avatar, hp);
        BasicCommands.setUnitAttack(out, avatar, atk);

        // Authoritative stats
        gs.unitHp.put(id, hp);
        gs.unitAtk.put(id, atk);
        gs.canMove.put(id, true);
        gs.canAttack.put(id, true);
        return avatar;
    }

    private void sleep(int ms) {
        try { Thread.sleep(ms); } catch (Exception e) {}
    }
}