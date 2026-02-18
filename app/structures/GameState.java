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
}