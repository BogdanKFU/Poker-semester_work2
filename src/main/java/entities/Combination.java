package entities;

import enums.Combinations;

import java.util.ArrayList;
import java.util.List;

public class Combination {
    private List<Card> cards;
    private Combinations combination;
    private int key = 0;

    public Combination() {
        this.cards = new ArrayList<>();
        this.combination = Combinations.HIGH_CARD;
    }

    public Combination(List<Card> cards, Combinations combination) {
        this.cards = new ArrayList<>();
        this.cards.addAll(cards);
        this.combination = combination;
    }

    public void add(Card card) {
        cards.add(card);
    }

    public Card pop() {
        return cards.remove(cards.size() - 1);
    }

    public List<Card> getCards() {
        return cards;
    }

    public Combinations getCombination() {
        return combination;
    }

    public int size() {
        return cards.size();
    }
}
