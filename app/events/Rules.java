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
		return ValidationResult.fail("validateMove not implemented");
	}

	public static ValidationResult validateAttack(GameState gs, Unit attacker, Unit defender) {
		return ValidationResult.fail("validateAttack not implemented");
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
}