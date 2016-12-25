package classes;

import entities.Card;
import entities.Combination;
import enums.Combinations;

import java.util.*;

/*
Singleton
 */
public class CombinationsChecker {

    private Stack<Connection> winner = new Stack<>();


    public static int compare(Connection c1, Connection c2, Game game) {
        List<Card> c1_cards = new ArrayList<>();
        c1_cards.addAll(game.getBoard());
        c1_cards.add(c1.getPlayer().getFirst_card());
        c1_cards.add(c1.getPlayer().getSecond_card());
        List<Card> c2_cards = new ArrayList<>();
        c2_cards.add(c2.getPlayer().getFirst_card());
        c2_cards.add(c2.getPlayer().getSecond_card());
        c2_cards.addAll(game.getBoard());
        Collections.sort(c1_cards);
        Collections.sort(c2_cards);
        for (int i = 0; i < 7; i++) {
            if (c1_cards.get(i).compareTo(c2_cards.get(i)) != 0) {
                return c1_cards.get(i).compareTo(c2_cards.get(0));
            }
        }
        return 0;
    }
    public static int i = 0;

    public static Stack<Connection> getWinner(Game game) {
        List<Connection> connections = game.getRoom().getConnections();
        List<Card> cards = game.getBoard();
        for (Connection conn : connections) {
            if (!conn.getPlayer().isFolded()) {
                cards.add(conn.getPlayer().getFirst_card());
                cards.add(conn.getPlayer().getSecond_card());
                List<Card> clone = new ArrayList<>();
                clone.addAll(cards);
                for (Card card : cards) {
                    System.out.println(card.toString());
                }
                Combination comb = findHighestCombination(clone);
                cards.remove(cards.size() - 1);
                cards.remove(cards.size() - 1);
                conn.getPlayer().setBest_comb(comb);
            }
        }
        int max = 0;
        Stack<Connection> winners = new Stack<>();
        for (Connection conn : connections) {
            if (!conn.getPlayer().isFolded()) {
                int priority = conn.getPlayer().getBest_comb().getCombination().getPriority();
                if (priority > max) {
                    winners = new Stack<>();
                    winners.add(conn);
                    max = priority;
                } else if (priority == max) {
                    winners.add(conn);
                }
            }
        }
        Connection previous = winners.pop();
        Stack<Connection> best = new Stack<>();
        best.add(previous);
        while (winners.size() > 0) {
            Connection conn = winners.pop();
            if (compare(previous, conn, game) > 0) {
                best = new Stack<>();
                best.add(conn);
            } else if (compare(previous, conn, game) == 0) {
                best.add(conn);
            }
        }
        return best;
    }

    /*
    Находит лучшую комбинацию
     */
    public static Combination findHighestCombination(List<Card> list) {
        for (Card card: list) {
            System.out.println(card.toString() + i);
        }
        Collections.sort(list);
        Card previous = list.get(0);
        Combination alike = new Combination();
        Combination sequence = new Combination();
        alike.add(previous);
        sequence.add(previous);
        Combination best_comb = new Combination();
        best_comb.add(previous);
        HashMap<String, List<Card>> map = new HashMap<>();
        map.put("♥", new ArrayList<>());
        map.put("♦", new ArrayList<>());
        map.put("♣", new ArrayList<>());
        map.put("♠", new ArrayList<>());
        for (Card card : list) {
            map.get(card.getSuit().getSuit()).add(card);
        }
        /*
        Проверка на флеш
         */
        for (String str : map.keySet()) {
            List<Card> cards = map.get(str);
            if (cards.size() >= 5) {
                best_comb = new Combination(cards, Combinations.FLUSH);
                if (isStraight(cards)) {
                    best_comb = new Combination(cards, Combinations.STRAIGHT_FLUSH);
                }
            }
        }
        /*
        Проверка на стрит
         */
        if (!(best_comb.getCombination().getPriority() > Combinations.STRAIGHT.getPriority())) {
            if (isStraight(list)) {
                best_comb = new Combination(list, Combinations.STRAIGHT);
            }
        }
        if (best_comb.getCombination().getPriority() != Combinations.STRAIGHT_FLUSH.getPriority()) {
            HashMap<Integer, List<Card>> cards = new HashMap<>();
            cards.put(1, new ArrayList<>());
            cards.put(2, new ArrayList<>());
            Card previous_card = list.get(0);
            int key = 1;
            cards.get(key).add(previous_card);
            for (int i = 1; i < list.size() && key < 3; i++) {
                Card card = list.get(i);
                if ((int) previous_card.getCard().getPriority() == card.getCard().getPriority()) {
                    cards.get(key).add(card);
                } else if (cards.get(key).size() >= 2 && cards.get(key).size() < 4) {
                    key++;
                    if (key < 3) {
                        cards.get(key).add(card);
                    }
                } else if (cards.get(key).size() < 4) {
                    cards.remove(key);
                    cards.put(key, new ArrayList<>());
                    cards.get(key).add(card);
                }
                previous_card = card;
            }
            if (cards.get(1).size() == 4) {
                if (best_comb.getCombination().getPriority() < Combinations.FOUR_OF_A_KING.getPriority())
                    best_comb = new Combination(cards.get(1), Combinations.FOUR_OF_A_KING);
            } else if (cards.get(1).size() == 3) {
                if (best_comb.getCombination().getPriority() < Combinations.THREE_OF_A_KING.getPriority())
                    best_comb = new Combination(cards.get(1), Combinations.THREE_OF_A_KING);
                if (cards.get(2).size() == 2) {
                    if (best_comb.getCombination().getPriority() < Combinations.FULL_HOUSE.getPriority()) {
                        cards.get(1).addAll(cards.get(2));
                        best_comb = new Combination(cards.get(1), Combinations.FULL_HOUSE);
                    }
                }
            } else if (cards.get(1).size() == 2) {
                if (best_comb.getCombination().getPriority() < Combinations.ONE_PAIR.getPriority())
                    best_comb = new Combination(cards.get(1), Combinations.ONE_PAIR);
                if (cards.get(2).size() == 2) {
                    if (best_comb.getCombination().getPriority() < Combinations.THREE_OF_A_KING.getPriority()) {
                        cards.get(1).addAll(cards.get(2));
                        best_comb = new Combination(cards.get(1), Combinations.TWO_PAIR);
                    }
                } else if (cards.get(2).size() == 3) {
                    cards.get(1).addAll(cards.get(2));
                    if (best_comb.getCombination().getPriority() < Combinations.FULL_HOUSE.getPriority()) {
                        best_comb = new Combination(cards.get(1), Combinations.FULL_HOUSE);
                    }
                }
            } else {
                List<Card> high_card = new ArrayList<>();
                high_card.add(list.get(0));
                if (best_comb.getCombination().getPriority() < Combinations.HIGH_CARD.getPriority())
                    best_comb = new Combination(high_card, Combinations.HIGH_CARD);
            }
        }
        i++;
        return best_comb;
    }

    public static boolean isStraight(List<Card> list) {
        Card previous_card = list.get(0);
        int seq = 0;
        for(int i = 1; i < list.size(); i++) {
            Card card = list.get(i);
            if (previous_card.getCard().getPriority() - 1 == card.getCard().getPriority()) {
                seq++;
            }
        }
        return seq == 5;
    }
}
