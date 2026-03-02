package events;

import java.util.Collections;
import java.util.List;

import structures.GameState;
import structures.basic.Card;
import structures.basic.Tile;
import structures.basic.Unit;

/**
 * Central rules/validators skeleton.
 * Implementations are added in later sprints.
 */
public final class Rules {

	private Rules() {}

	public static final class ValidationResult {
		public final boolean ok;
		public final String reason;

		private ValidationResult(boolean ok, String reason) {
			this.ok = ok;
			this.reason = reason;
		}

		public static ValidationResult ok() { return new ValidationResult(true, null); }
		public static ValidationResult fail(String reason) { return new ValidationResult(false, reason); }
	}

	public static ValidationResult validateMove(GameState gs, Unit unit, Tile targetTile) {
		if (gs == null || unit == null || targetTile == null) return ValidationResult.fail("null args");
		if (unit.getPosition() == null) return ValidationResult.fail("unit has no position");

		Tile from = gs.board.getTile(unit.getPosition().getTilex(), unit.getPosition().getTiley());
		if (from == null) return ValidationResult.fail("from tile not found");

		boolean ok = isValidMove(gs, unit.getId(), from, targetTile);
		return ok ? ValidationResult.ok() : ValidationResult.fail("invalid move");
	}

	public static ValidationResult validateAttack(GameState gs, Unit attacker, Unit defender) {
		if (gs == null || attacker == null || defender == null) return ValidationResult.fail("null args");
		if (!gs.canUnitAttack(attacker.getId())) return ValidationResult.fail("attacker cannot attack");
		if (!gs.isTargetValidAndInRange(attacker, defender)) return ValidationResult.fail("out of range");

		boolean sameSide = isHumanUnit(gs, attacker) == isHumanUnit(gs, defender);
		if (sameSide) return ValidationResult.fail("friendly target");

		return ValidationResult.ok();
	}

	public static ValidationResult validateSummon(GameState gs, Card card, Tile targetTile) {
		return ValidationResult.fail("validateSummon not implemented");
	}

	public static ValidationResult validateSpellTarget(GameState gs, Card spellCard, Unit unitTarget, Tile tileTarget) {
		return ValidationResult.fail("validateSpellTarget not implemented");
	}

	public static List<Tile> applyKeywordRestrictions(GameState gs, Unit unit, List<Tile> candidates) {
		return candidates == null ? Collections.emptyList() : candidates;
	}

	// API stubs - used by processors
	public static List<Tile> getValidMoveTiles(GameState gs, int unitId) {
		if (gs == null || gs.board == null) return Collections.emptyList();
		if (!gs.canUnitMove(unitId)) return Collections.emptyList();

		Unit unit = findUnitById(gs, unitId);
		if (unit == null || unit.getPosition() == null) return Collections.emptyList();

		int ux = unit.getPosition().getTilex();
		int uy = unit.getPosition().getTiley();

		boolean isAvatar = isAvatarUnit(gs, unitId);

		java.util.ArrayList<Tile> candidates = new java.util.ArrayList<>();

		// Build candidate deltas based on unit type
		java.util.ArrayList<int[]> deltas = new java.util.ArrayList<>();

		if (isAvatar) {
			// 1-step around (includes diagonals)
			for (int dx = -1; dx <= 1; dx++) {
				for (int dy = -1; dy <= 1; dy++) {
					if (dx == 0 && dy == 0) continue;
					deltas.add(new int[]{dx, dy});
				}
			}
			// + 2-step orthogonal (no diagonal)
			deltas.add(new int[]{ 2,  0});
			deltas.add(new int[]{-2,  0});
			deltas.add(new int[]{ 0,  2});
			deltas.add(new int[]{ 0, -2});
		} else {
			// Normal units: 1-step around (includes diagonals)
			for (int dx = -1; dx <= 1; dx++) {
				for (int dy = -1; dy <= 1; dy++) {
					if (dx == 0 && dy == 0) continue;
					deltas.add(new int[]{dx, dy});
				}
			}
		}

		for (int[] d : deltas) {
			int nx = ux + d[0];
			int ny = uy + d[1];

			if (!gs.board.inBounds(nx, ny)) continue;
			if (gs.board.getUnitAt(nx, ny) != null) continue;

			Tile t = gs.board.getTile(nx, ny);
			if (t != null) candidates.add(t);
		}

		return applyKeywordRestrictions(gs, unit, candidates);
	}

	public static boolean isValidMove(GameState gs, int unitId, Tile from, Tile to) {
		if (gs == null || gs.board == null) return false;
		if (from == null || to == null) return false;
		if (!gs.canUnitMove(unitId)) return false;

		Unit unit = findUnitById(gs, unitId);
		if (unit == null || unit.getPosition() == null) return false;

		int ux = unit.getPosition().getTilex();
		int uy = unit.getPosition().getTiley();

		// must start from the unit's current tile
		if (from.getTilex() != ux || from.getTiley() != uy) return false;

		int dx = to.getTilex() - from.getTilex();
		int dy = to.getTiley() - from.getTiley();
		int adx = Math.abs(dx);
		int ady = Math.abs(dy);

		if (adx == 0 && ady == 0) return false;

		boolean isAvatar = isAvatarUnit(gs, unitId);

		boolean movementOk;
		if (isAvatar) {
			// Avatar: 1-step any direction (incl diagonal)
			boolean oneStep = (adx <= 1 && ady <= 1 && !(adx == 0 && ady == 0));
			// Avatar: 2-step orthogonal only
			boolean twoStepOrth = (adx == 2 && ady == 0) || (adx == 0 && ady == 2);
			movementOk = oneStep || twoStepOrth;
		} else {
			// Normal units: 1-step any direction (incl diagonal)
			movementOk = (adx <= 1 && ady <= 1 && !(adx == 0 && ady == 0));
		}

		if (!movementOk) return false;

		// must be in bounds and unoccupied
		int tx = to.getTilex();
		int ty = to.getTiley();
		if (!gs.board.inBounds(tx, ty)) return false;
		if (gs.board.getUnitAt(tx, ty) != null) return false;

		return true;
	}

	public static List<Unit> getValidAttackTargets(GameState gs, int unitId) {
		if (gs == null || gs.board == null) return Collections.emptyList();
		if (!gs.canUnitAttack(unitId)) return Collections.emptyList();

		Unit attacker = findUnitById(gs, unitId);
		if (attacker == null || attacker.getPosition() == null) return Collections.emptyList();

		boolean attackerIsHuman = isHumanUnit(gs, attacker);

		int ax = attacker.getPosition().getTilex();
		int ay = attacker.getPosition().getTiley();

		java.util.ArrayList<Unit> targets = new java.util.ArrayList<>();

		// melee range: 1 tile around (including diagonal)
		for (int dx = -1; dx <= 1; dx++) {
			for (int dy = -1; dy <= 1; dy++) {
				if (dx == 0 && dy == 0) continue;

				int nx = ax + dx;
				int ny = ay + dy;

				if (!gs.board.inBounds(nx, ny)) continue;

				Unit maybe = gs.board.getUnitAt(nx, ny);
				if (maybe == null) continue;
				if (maybe.getId() == unitId) continue;

				// enemy-only
				boolean targetIsHuman = isHumanUnit(gs, maybe);
				if (attackerIsHuman == targetIsHuman) continue;

				targets.add(maybe);
			}
		}

		return targets;
	}

	// ---------- helpers (private) ----------
	private static boolean isAvatarUnit(GameState gs, int unitId) {
		return (gs.humanAvatar != null && gs.humanAvatar.getId() == unitId)
			|| (gs.aiAvatar != null && gs.aiAvatar.getId() == unitId);
	}

	private static boolean isHumanUnit(GameState gs, Unit u) {
		if (gs == null || u == null) return false;
		int id = u.getId();
		if (gs.humanAvatar != null && gs.humanAvatar.getId() == id) return true;
		for (Unit hu : gs.humanUnits) if (hu.getId() == id) return true;
		return false;
	}

	private static Unit findUnitById(GameState gs, int id) {
		if (gs.humanAvatar != null && gs.humanAvatar.getId() == id) return gs.humanAvatar;
		if (gs.aiAvatar != null && gs.aiAvatar.getId() == id) return gs.aiAvatar;

		for (Unit u : gs.humanUnits) if (u.getId() == id) return u;
		for (Unit u : gs.aiUnits) if (u.getId() == id) return u;

		return null;
	}
}