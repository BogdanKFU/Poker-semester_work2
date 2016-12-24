package enums;

public enum Suits {
    HEART('\u2764'), DIAMOND('\u2666'), CLUB('\u2663'), SPADE('\u2660');

    private final char suit;

    Suits(char suit) {
        this.suit = suit;
    }

    public char getSuit() {
        return suit;
    }
}
