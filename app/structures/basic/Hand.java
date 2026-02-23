package structures.basic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Hand{
    public static final int MAX_SIZE = 6;
    private final List<Card> cards = new ArrayList<>();

    //return true if card was added, false if hand is full
    public boolean add(Card c){
        if (c == null) return false;
        if (cards.size() >= MAX_SIZE) return false;
        cards.add(c);
        return true;
    }
    public Card remove(int index){
        return cards.remove(index);
    }
    public boolean remove(Card c) {
        return cards.remove(c);
    }
    public List<Card> getCards() {
        return Collections.unmodifiableList(cards);
    }
    public int size() {
        return cards.size();
    }
}