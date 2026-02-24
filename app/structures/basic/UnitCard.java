package structures.basic;

public class UnitCard extends Card {
    public UnitCard() {
        super();
    }
    public UnitCard(Card c) {
        super(
            c.getId(),
            c.getCardname(),
            c.getManacost(),
            c.getMiniCard(),
            c.getBigCard(),
            true,
            c.getUnitConfig()
        );
        this.setCreature(true);
    }
}