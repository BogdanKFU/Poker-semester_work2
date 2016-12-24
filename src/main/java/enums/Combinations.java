package enums;

public enum Combinations {
    HIGH_CARD(0), ONE_PAIR(1), TWO_PAIR(2), THREE_OF_A_KING(3), STRAIGHT(4), FLUSH(5), FULL_HOUSE(6),
    FOUR_OF_A_KING(7), STRAIGHT_FLUSH(8);

    private final int priority;

    Combinations(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }
}
