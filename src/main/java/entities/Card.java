package entities;

import enums.Cards;
import enums.Suits;

/*
Карта для игры в покер
 */
public class Card implements Comparable {
    /*
    private String name = "";
    private String ASCII;
     */
    private Cards card;
    private Suits suit;

    public Card(Cards card, Suits suit) {
        this.card = card;
        this.suit = suit;
    }

    public String toString() {
        return card.getName() + suit.getSuit() + " ";
    }

    public Cards getCard() {
        return card;
    }

    public Suits getSuit() {
        return suit;
    }

    @Override
    public int compareTo(Object o) {
        Card card = (Card) o;
        return card.getCard().getPriority().compareTo(this.card.getPriority());
    }
}
