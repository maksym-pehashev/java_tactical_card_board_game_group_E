package structures.basic;

public class CardFactory{
    // Factory: convert loaded Card into UnitCard or SpellCard
    public static Card toTypedCard(Card c) {
        if (c == null) return null;
        // Creature cards must have unitConfig
        if (c.isCreature()) {
            return new UnitCard(c);
        }

        return new SpellCard(c);
    }
}