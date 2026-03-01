package structures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import structures.basic.Board;
import structures.basic.Card;
import structures.basic.Player;
import structures.basic.Tile;
import structures.basic.Unit;

public class GameState {

	
	public boolean gameInitalised = false;
	

	public boolean gameOver = false;
	public String winner = null;   // "human" / "ai" / null
	public boolean humanTurn = true;
	public boolean aiTurnPending = false;  //AI placeholder: run once on next heartbeat

	// Turn info
	public int turnNumber = 0;
	public static final int MAX_MANA_CAP = 9;

	// Players
	public Player player1; // human
	public Player player2; // ai

	public int humanMaxMana = 0;
	public int aiMaxMana = 0;
	public int humanCurrentMana = 0;
	public int aiCurrentMana = 0;

	// Board
	public Board board;

	// Avatars
	public Unit humanAvatar;
	public Unit aiAvatar;

	// Units (excluding avatars)
	public List<Unit> humanUnits = new ArrayList<>();
	public List<Unit> aiUnits = new ArrayList<>();

	// Authoritative per-unit stats & action flags
	public Map<Integer, Integer> unitHp = new HashMap<>();
	public Map<Integer, Integer> unitAtk = new HashMap<>();
	public Map<Integer, Boolean> canMove = new HashMap<>();
	public Map<Integer, Boolean> canAttack = new HashMap<>();
	public Map<Integer, Boolean> exhausted = new HashMap<>();
	public Map<Integer, Boolean> summoningSickness = new HashMap<>();

	// Card containers are present as lists only (logic handled by Xinyu/Yanfei)
	public List<Card> humanDeck = new ArrayList<>();
	public List<Card> humanHand = new ArrayList<>();
	public List<Card> humanDiscard = new ArrayList<>();

	public List<Card> aiDeck = new ArrayList<>();
	public List<Card> aiHand = new ArrayList<>();
	public List<Card> aiDiscard = new ArrayList<>();

	// IDs
	public int nextUnitId = 10;
	public int nextCardId = 1;

	// Interaction context (multi-step UI flows)
	public Unit selectedUnit = null;
	public Card selectedCard = null;
	public List<Tile> highlightedTiles = new ArrayList<>();

	public void clearSelection() {
		selectedUnit = null;
		selectedCard = null;
	}

	public void clearHighlights() {
		highlightedTiles.clear();
	}

	public void clearSelectionAndHighlights() {
		clearSelection();
		clearHighlights();
	}
	// this method can check whether a unit can move or not.
	public boolean canUnitMove(int unitId) {
		if (Boolean.TRUE.equals(summoningSickness.get(unitId))) return false;
		if (Boolean.TRUE.equals(exhausted.get(unitId))) return false;
		return Boolean.TRUE.equals(canMove.get(unitId));
	}

	// this method can check whether a unit can attack or not
	public boolean canUnitAttack(int unitId) {
		if (Boolean.TRUE.equals(summoningSickness.get(unitId))) return false;
		if (Boolean.TRUE.equals(exhausted.get(unitId))) return false;
		return Boolean.TRUE.equals(canAttack.get(unitId));
	}

	// this method can check whether a unit can be a valid target to attack \ check the target whether in range
	public boolean isTargetValidAndInRange(Unit attacker, Unit target) {
		if (attacker == null || target == null) return false;
		if (attacker.getId() == target.getId()) return false;
		if (attacker.getPosition() == null || target.getPosition() == null) return false;

		int ax = attacker.getPosition().getTilex();
		int ay = attacker.getPosition().getTiley();
		int tx = target.getPosition().getTilex();
		int ty = target.getPosition().getTiley();

		return Math.abs(tx - ax) <= 1 && Math.abs(ty - ay) <= 1;
	}
	// if unit already move, use this method can make this unit cannot move again during same turn
	// but it can still attack
	public void onMoveDone(int unitId){
		canMove.put(unitId, false);
	}
	// if unit already attack, use this method can make this unit cannot move or attack again during same turn
	public void onAttackDone(int unitId){
		canAttack.put(unitId,false);
		canMove.put(unitId, false);
	}
	// can use this method to reset all units state
	public void resetFlagsAtTurnStart() {
        for (Integer unitId:canMove.keySet()) {
            canMove.put(unitId, true);
        }
        for (Integer unitId: canAttack.keySet()) {
            canAttack.put(unitId, true);
        }
        for (Integer unitId:summoningSickness.keySet()) {
            summoningSickness.put(unitId, false);
        }
        for (Integer unitId:exhausted.keySet()) {
            exhausted.put(unitId, false);
        }
	}
}	