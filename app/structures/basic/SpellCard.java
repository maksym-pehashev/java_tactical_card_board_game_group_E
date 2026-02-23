package structures.basic;

public class SpellCard extends Card {
    public SpellCard() {
        super();
    }
    public SpellCard(Card c) {
        super(
            c.getId(),
            c.getCardname(),
            c.getManacost(),
            c.getMiniCard(),
            c.getBigCard(),
            false,
            null
        );
        this.setCreature(false);
        this.setUnitConfig(null);
    }
}