package enums;

public enum Cards {
    TWO("2", 0), THREE("3", 1), FOUR("4", 2), FIVE("5", 3), SIX("6", 4), SEVEN("7", 5), EIGHT("8", 6),
    NINE("9", 7), TEN("10", 8), JACK("J", 9), QUEEN("Q", 10), KING("K", 11), ACE("A", 12);

    private final String name;
    private final Integer priority;

    Cards(String name, Integer priority) {
        this.name = name;
        this.priority = priority;
    }

    public String getName() {
        return name;
    }

    public Integer getPriority() {
        return priority;
    }
}
