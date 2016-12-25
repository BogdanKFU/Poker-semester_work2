package enums;

public enum Suits {
    HEART("♥"), DIAMOND("♦"), CLUB("♣"), SPADE("♠");

    private final String suit;

    Suits(String suit) {
        this.suit = suit;
    }

    public String getSuit() {
        return suit;
    }
}
