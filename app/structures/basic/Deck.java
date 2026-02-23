package structures.basic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck{
    private final List<Card> cards = new ArrayList<>();
    public void addAll(List<? extends Card> cs){
        if (cs!=null) cards.addAll(cs);
    }
    public void add(Card c) {
        if (c!= null) cards.add(c);
    }
    public void shuffle() {
        Collections.shuffle(cards);
    }
    public Card draw() {
        if (cards.isEmpty()) return null;
        return cards.remove(0);
    }
    public boolean isEmpty() {
        return cards.isEmpty();
    }
    public List<Card> getCards() {
        return Collections.unmodifiableList(cards);
    }
}
