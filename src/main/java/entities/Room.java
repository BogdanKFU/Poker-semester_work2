package entities;

import classes.Connection;
import classes.Game;
import enums.Cards;
import enums.Suits;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/*
Комната для игры
 */
public class Room {
    private int id = 0;
    private Stack<Card> deck = new Stack<>();
    private List<Connection> connections = new ArrayList<>();
    private Game game = null;
    private static final int CARDS_SIZE = 13;
    private static final int SUIT_SIZE = 4;

    private void init() {
        for (int i = 0; i < CARDS_SIZE; i++) {
            for (int j = 0; j < SUIT_SIZE; j++) {
                deck.add(new Card(Cards.values()[i], Suits.values()[j]));
            }
        }
    }
    
    public Room(int id) {
        this.id = id;
        init();
    }

    // Добавляет socket в список сокетов socket_list
    public void add_player(Connection conn) {
        connections.add(conn);
    }

    public void remove_player(Connection conn) {
        connections.remove(conn);
    }


    public int getId() {
        return id;
    }

    public List<Connection> getConnections() {
        return connections;
    }

    public void newGame() {
        if (game == null) {
            game = new Game(this);
        }
        if (connections.size() > 1 && !game.isAlive()) {
            game.play();
        }
    }

    public Stack<Card> getDeck() {
        return deck;
    }

    public Game getGame() {
        return game;
    }

    public void deleteGame() {
        this.game = null;
    }
}
