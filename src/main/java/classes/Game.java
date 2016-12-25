package classes;


import entities.Card;
import entities.Player;
import entities.Room;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Game implements Runnable {
    private Thread thread;
    private Room room;
    private Stack<Card> discarded = new Stack<>();
    private int pot;
    private final Pattern raise = Pattern.compile("raise (?<amount>[1-9]?[0-9]*)");
    private final static int MIN_BET = 10;
    private Integer max = 0;
    private int starter;
    private List<Card> board = new ArrayList<>();
    private Stack<Connection> winners = new Stack<>();

    public Game(Room room) {
        thread = new Thread(this);
        this.room = room;
        this.board = new ArrayList<>();
    }

    public void setMax(Integer max) {
        this.max = max;
    }

    public void refresh(){
        Stack<Card> deck = room.getDeck();
        Collections.shuffle(deck);
    }

    /*
    Проверяет, равны ли ставки
     */
    private void check(Player player) {
        int bet = player.getBet();
        if (bet > max) {
            max = bet;
        }
    }

    public boolean check_bets() {
        List<Connection> connections = room.getConnections();
        int i = 0;
        for (Connection conn: connections) {
            /*
            Здесь проверка на сброшенные карты. Если игрок сбросил карты - то уже с него ничего не возьмешь
             */
            if (!conn.getPlayer().isFolded()) {
                i++;
                if (conn.getPlayer().getBet() < max && conn.getPlayer().getMoney() != 0) {
                    return false;
                }
            }
        }
        if (i == 1) {
            event(5);
        }
        return true;
    };

    @Override
    public void run() {
        try {
            while(room.hasParticipates()) {
                List<Connection> connections = room.getConnections();
                PrintWriter this_out;
                for (int i = 0; i < 4; i++) {
                    for (Connection conn : connections) {
                        this_out = new PrintWriter(conn.getSocket().getOutputStream(), true);
                        if (i == 0) {
                            this_out.println("Hi! One moment, please. The game will start after 3 seconds.");
                        } else {
                            this_out.println(i);
                        }
                    }
                    Thread.sleep(1000);
                }
                pot = 0;
                for (Connection conn : connections) {
                    conn.getPlayer().setMoney(10000);
                    conn.getPlayer().refresh();
                }
                refresh();
            /*
            Пять кругов торгов
             */
                for (int i = 0; i < 5 && winners.size() == 0; i++) {
                /*
                Торги
                 */
                    for (Connection conn : connections) {
                        this_out = new PrintWriter(conn.getSocket().getOutputStream(), true);
                        this_out.println("Hi!");
                    }
                    Player player = null;
                /*
                События
                 */
                    event(i);
                    if (starter != 0) {
                        List<Connection> subList1 = connections.subList(starter, connections.size() - 1);
                        List<Connection> subList2 = connections.subList(0, starter - 1);
                        if (!ring(subList1)) return;
                        if (!ring(subList2)) return;
                    } else {
                        if (!ring(connections)) return;
                    }
                    int k = starter;
                    while (!check_bets()) {
                        if (k == connections.size()) {
                            k = 0;
                        }
                        Connection conn = connections.get(k);
                        if (!turn(conn)) return;
                        k++;
                    }
                }
                for (Connection conn: connections) {
                    pot += conn.getPlayer().getBet();
                }
                if (winners.size() == 0) {
                    this.winners = CombinationsChecker.getWinner(this);
                    while (winners.size() > 0) {
                        Connection conn = winners.pop();
                        sendToOpponents("WINNERS: " + conn.getPlayer().getName(), null);
                        sendToOpponents("THE COMBINATION IS " + conn.getPlayer().getBest_comb().getCombination().toString(), null);
                        sendToOpponents("POT: " + pot, null);
                    }
                }
                clear();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void clear() {
        Stack<Card> deck = room.getDeck();
        deck.addAll(discarded);
    }

    public void play() {
        thread.start();
    }

    public boolean isAlive() {
        return thread.isAlive();
    }

    public Integer getMax() {
        return max;
    }

    public Thread getThread() {
        return thread;
    }

    public void sendToOpponents(String string, Connection sender) {
        List<Connection> connections = room.getConnections();
        try {
            if (sender != null) {
                for (Connection conn : connections) {
                    if (conn != sender) {
                        PrintWriter out = new PrintWriter(conn.getSocket().getOutputStream(), true);
                        out.println("Player " + sender.getPlayer().getName() + " " + string);
                    }
                }
            }
            else {
                for (Connection conn : connections) {
                    PrintWriter out = new PrintWriter(conn.getSocket().getOutputStream(), true);
                    out.println(string);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean turn(Connection conn) {
        try {
            Player player;
            if (!conn.getPlayer().isFolded()) {
                do {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getSocket().getInputStream()));
                    PrintWriter out = new PrintWriter(conn.getSocket().getOutputStream(), true);
                    String str = in.readLine();
                    Matcher mather = raise.matcher(str);
                    player = conn.getPlayer();
                    if (mather.matches()) {
                        try {
                            player.raise(Integer.valueOf(mather.group("amount")));
                        } catch (Exception e) {
                            out.println(e.toString());
                        }
                        sendToOpponents("raised for " + mather.group("amount"), conn);
                        player.setTurned();
                    } else if (str.equals("call")) {
                        try {
                            player.call();
                        } catch (Exception e) {
                            out.println(e.toString());
                        }
                        sendToOpponents("called", conn);
                        player.setTurned();
                    } else if (str.equals("check")) {
                        try {
                            player.check();
                        } catch (Exception e) {
                            out.println(e.toString());
                        }
                        sendToOpponents("checked", conn);
                        player.setTurned();
                    } else if (str.equals("all-in")) {
                        player.all_in();
                        sendToOpponents("bet all", conn);
                        player.setTurned();
                    } else if (str.equals("fold")) {
                        try {
                            player.fold();
                        } catch (Exception e) {
                            out.println(e.toString());
                        }
                        sendToOpponents("folded", conn);
                        player.setTurned();
                    } else if (str.equals("exit from game")) {
                        if (!exit(conn)) {
                            return exit(conn);
                        }
                        break;
                    } else if (str.equals("get max")) {
                        new PrintWriter(conn.getSocket().getOutputStream(), true).println(max);
                    } else if (str.equals("get my bet")) {
                        new PrintWriter(conn.getSocket().getOutputStream(), true).println(conn.getPlayer().getBet());
                    }
                }
                while (!player.isTurned());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public boolean exit(Connection conn) {
        synchronized (conn.getInGame()) {
            List<Connection> connections = room.getConnections();
            int size = connections.size();
            room.remove_player(conn);
            conn.setInGame(false);
            conn.getInGame().notify();
            if (size < 2) {
                room.deleteGame();
                return false;
            }
        }
        return true;
    }

    public boolean ring(List<Connection> list) {
        for(Connection conn: list) {
            boolean flag = turn(conn);
            check(conn.getPlayer());
            if (!flag) {
                return false;
            }
        }
        return true;
    }

    public void event(int i) {
        switch (i) {
            case 0:
                /*
                Здесь блайнды
                Выставление блайндов - начальных ставок. Два соседних игрока случайным образом. Первый - полная ставка.
                Второй - половина ставки. Игра начинается с первого игрока.
                 */
                try {
                    List<Connection> connections = room.getConnections();
                    Random random = new Random();
                    int num = random.nextInt(connections.size() - 1);
                    Connection conn = connections.get(num);

                    PrintWriter out = new PrintWriter(conn.getSocket().getOutputStream(), true);

                    try {
                        conn.getPlayer().bet(MIN_BET);
                    } catch (Exception e) {
                        out.println(e.toString());
                    }
                    if (num + 1 == connections.size())  {
                        conn = connections.get(0);
                    }
                    else {
                        conn = connections.get(num + 1);
                    }
                    try {
                        conn.getPlayer().bet(MIN_BET / 2);
                    } catch (Exception e) {
                        out.println(e.toString());
                    }
                    starter = num;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case 1:
                /*
                Здесь раздача двух карт
                 */
                try {
                    PrintWriter this_out;
                    Stack<Card> deck = room.getDeck();
                    List<Connection> connections1 = room.getConnections();
                    for (Connection conn1: connections1) {
                        this_out = new PrintWriter(conn1.getSocket().getOutputStream(), true);
                        Card card = deck.pop();
                        this_out.println(card.toString());
                        conn1.getPlayer().setFirst_card(card);
                        card = deck.pop();
                        this_out.println(card.toString());
                        conn1.getPlayer().setSecond_card(card);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case 2:
                /*
                Здесь раздача трех карт на столе
                 */
                try {
                    PrintWriter this_out;
                    Stack<Card> deck = room.getDeck();
                    List<Connection> connections1 = room.getConnections();
                    for (int j = 0; j < 3; j++) {
                        Card card = deck.pop();
                        board.add(card);
                        for (Connection conn1 : connections1) {
                            this_out = new PrintWriter(conn1.getSocket().getOutputStream(), true);
                            this_out.println(card.toString());
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case 3:case 4:
                /*
                Здесь раздача еще одной карты на столе
                 */
                try {
                    PrintWriter this_out;
                    Stack<Card> deck = room.getDeck();
                    List<Connection> connections1 = room.getConnections();
                    Card card = deck.pop();
                    board.add(card);
                    for (Connection conn1: connections1) {
                        this_out = new PrintWriter(conn1.getSocket().getOutputStream(), true);
                        this_out.println(card.toString());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case 5:
                List<Connection> connections = room.getConnections();
                for (Connection conn: connections) {
                    pot += conn.getPlayer().getBet();
                }
                for (Connection conn: connections) {
                    if (!conn.getPlayer().isFolded()) {
                        winners.add(conn);
                        sendToOpponents("WINNER: " + conn.getPlayer().getName() +
                                " ALL OTHER PLAYER FOLDED THEIR CARDS." +
                                "POT: " + pot, null);
                    }
                }
            default:
                break;
        }
    }

    public List<Card> getBoard() {
        return board;
    }

    public Room getRoom() {
        return room;
    }

    public Stack<Card> getDiscarded() {
        return discarded;
    }
}
