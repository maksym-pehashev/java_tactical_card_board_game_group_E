package structures;

import static org.junit.Assert.fail;

// this file store the state of the unit;
// a unit can move,attack,exhausted or just been summon that can not move in this turn.
public class UnitActionState {
    public Boolean canMove;
    public boolean canAttack;
    public boolean exhausted;
    public boolean summoningSickness;

// unit origin state
// unit that just has been summoned cannot move in the same turn
    public UnitActionState(){
        this.canMove=false;
        this.canAttack=false;
// if some unit or spell can make another unit cannot move or attack, can set exhausted to true.
        this.exhausted=false;
        this.summoningSickness=true;
    }
// during the next turn unit state change, can move and attack
    public void restForNewTurn(){
        this.summoningSickness=false;
        this.exhausted=false;
        this.canMove=true;
        this.canAttack=true;
    }
}
