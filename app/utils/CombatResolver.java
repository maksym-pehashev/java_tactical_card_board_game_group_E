package utils;

import structures.GameState;
import structures.basic.Unit;
import structures.basic.UnitAnimationType;
import commands.BasicCommands;
import akka.actor.ActorRef;

import java.util.ArrayList;
import java.util.List;

public class CombatResolver {

    public static void attack(int attackerId, int defenderId, GameState gs, ActorRef out) {
        Unit attacker = findUnitById(attackerId, gs);
        Unit defender = findUnitById(defenderId, gs);

        if (!gs.humanTurn) return;
        if (gs == null || out == null) return;
        if (gs.gameOver) return;
        if (attacker == null || defender == null) return;

        // 1) Prevent self-attack
        if (attackerId == defenderId) return;

        // 2) Attacker must be alive
        int currentAttackerHp = gs.unitHp.getOrDefault(attackerId, 0);
        if (currentAttackerHp <= 0) return;

        // 3) Eligibility gate 
        if (!gs.canUnitAttack(attackerId)) return;

        // 4) Range check for the main attack 
        if (!gs.isTargetValidAndInRange(attacker, defender)) return;

        // --- Resolve main damage ---
        int attackerAtk = gs.unitAtk.getOrDefault(attackerId, 0);
        int currentDefenderHp = gs.unitHp.getOrDefault(defenderId, 0);
        int newDefenderHp = Math.max(0, currentDefenderHp - attackerAtk);
        gs.unitHp.put(defenderId, newDefenderHp);

        BasicCommands.playUnitAnimation(out, attacker, UnitAnimationType.attack);
        sleep(1000);
        BasicCommands.playUnitAnimation(out, defender, UnitAnimationType.hit);
        sleep(500);
        BasicCommands.setUnitHealth(out, defender, newDefenderHp);

        // Mark attacker as having used attack this turn 
        gs.onAttackDone(attackerId);

        // Death check for defender 
        if (newDefenderHp <= 0) {
            DeathHandler.handleDeath(defender, gs, out);
            return; // No counter-attack possible if defender died
        }

        // --- Counter-Attack ---
        // Defender survives and attacker is still in defender's range (usually true, but keep it explicit)
        if (gs.isTargetValidAndInRange(defender, attacker)) {
            int defenderAtk = gs.unitAtk.getOrDefault(defenderId, 0);
            currentAttackerHp = gs.unitHp.getOrDefault(attackerId, 0);
            int newAttackerHp = Math.max(0, currentAttackerHp - defenderAtk);
            gs.unitHp.put(attackerId, newAttackerHp);

            BasicCommands.playUnitAnimation(out, defender, UnitAnimationType.attack);
            sleep(1000);
            BasicCommands.playUnitAnimation(out, attacker, UnitAnimationType.hit);
            sleep(500);
            BasicCommands.setUnitHealth(out, attacker, newAttackerHp);

            // Death check for attacker
            if (newAttackerHp <= 0) {
                DeathHandler.handleDeath(attacker, gs, out);
            }
        }
        // Post-combat idle animations (only if game not over, to avoid animation conflicts with endgame)
        if (!gs.gameOver) {
        BasicCommands.playUnitAnimation(out, attacker, UnitAnimationType.idle);
        if (gs.unitHp.getOrDefault(defender.getId(), 1) > 0) {
            BasicCommands.playUnitAnimation(out, defender, UnitAnimationType.idle);
        }
    }
    }

    private static Unit findUnitById(int id, GameState gs) {
        if (gs.humanAvatar != null && gs.humanAvatar.getId() == id) return gs.humanAvatar;
        if (gs.aiAvatar != null && gs.aiAvatar.getId() == id) return gs.aiAvatar;

        List<Unit> allUnits = new ArrayList<>();
        allUnits.addAll(gs.humanUnits);
        allUnits.addAll(gs.aiUnits);

        for (Unit u : allUnits) {
            if (u.getId() == id) return u;
        }
        return null;
    }

    private static void sleep(int ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { e.printStackTrace(); }
    }
}