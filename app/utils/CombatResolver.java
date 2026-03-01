package utils;

import structures.GameState;
import structures.basic.Unit;
import structures.basic.UnitAnimationType; 
import commands.BasicCommands;
import akka.actor.ActorRef;
import java.util.ArrayList;
import java.util.List;

/**
 * Core class to handle combat logic (SC#17 & #18).
 */
public class CombatResolver {

    public static void attack(int attackerId, int defenderId, GameState gs, ActorRef out) {
        Unit attacker = findUnitById(attackerId, gs);
        Unit defender = findUnitById(defenderId, gs);

        if (attacker == null || defender == null) return; // Safety check

        // Resolve combat damage
        int attackerAtk = gs.unitAtk.getOrDefault(attackerId, 0);
        int currentDefenderHp = gs.unitHp.getOrDefault(defenderId, 0);
        int newDefenderHp = Math.max(0, currentDefenderHp - attackerAtk);
        
        gs.unitHp.put(defenderId, newDefenderHp);

        // Play animations and update UI
        BasicCommands.playUnitAnimation(out, attacker, UnitAnimationType.attack);
        sleep(1000); 
        BasicCommands.playUnitAnimation(out, defender, UnitAnimationType.hit);
        sleep(500); 
        BasicCommands.setUnitHealth(out, defender, newDefenderHp);
        
        // TODO: Counter-Attack Logic (SC#18)
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

    // Utility method to keep the code clean
    private static void sleep(int ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { e.printStackTrace(); }
    }
}