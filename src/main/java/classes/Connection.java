package classes;



import entities.Card;
import entities.Player;
import entities.Room;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
Используется для соединения
classes.Connection - thread
 */
public class Connection implements Runnable {
    private Thread thread;
    private Socket socket;
    private int id;
    private List<Connection> connections;
    private final Pattern pattern = Pattern.compile("startgame (?<id>[0-9])");
    private Player player;
    private List<Room> rooms;
    private List<Card> cards;
    private Boolean inGame = false;

    public Connection(Socket socket, List<Connection> connections, int id, List<Room> list) {
        this.socket = socket;
        this.connections = connections;
        this.id = id;
        this.rooms = list;
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter this_out = new PrintWriter(socket.getOutputStream(), true);
            this_out.println("Hello! Please type your name:");
            String name = in.readLine();
            this_out.println("Hi, " + name + "! Welcome to Poker classes.Game! There is menu: help, rooms, startgame <room_id>. " +
                    "Please type your command");
            player = new Player();
            player.setName(name);
            while(true) {
                String string;
                try {
                    string = in.readLine();
                } catch (SocketException e) {
                    return;
                }
                Matcher matcher = pattern.matcher(string);
                /*
                Проверка: клиент в игре?
                 */
                this_out.println("HELLO!");
                if (!inGame) {
                    if (string.equals("exit")) {
                        String s = "Player id=" + Integer.toString(id) + " exited";
                        for (Connection conn : connections) {
                            if (conn != this) {
                                PrintWriter out = new PrintWriter(conn.getSocket().getOutputStream(), true);
                                out.println(s);
                            }
                        }
                        socket.close();
                        connections.remove(this);
                    } else if (matcher.matches()) {
                        try {
                            Room room = rooms.get(Integer.valueOf(matcher.group("id")));
                            room.add_player(this);
                            player.setRoom(room);
                            room.newGame();
                            synchronized (inGame) {
                                inGame.wait();
                            }
                        }
                        catch (IndexOutOfBoundsException e) {
                            this_out.println("main.java.ru.kpfu.itis.group11501.popov.entities.Room didn't found");
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else if (string.equals("rooms")) {
                        this_out.println("Rooms:");
                        for (Room room : rooms) {
                            this_out.println(room.getId());
                        }
                    }
                    else {
                        String s = player.getName() + ": " + string;
                        for (Connection conn : connections) {
                            if (conn != this) {
                                PrintWriter out = new PrintWriter(conn.getSocket().getOutputStream(), true);
                                out.println(s);
                            }
                        }
                    }
                }
                else {
                    /*
                    Тут распознавание команд для main.java.ru.kpfu.itis.group11501.popov.entities.Room
                     */
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected Socket getSocket() {
        return socket;
    }

    public Player getPlayer() {
        return player;
    }

    public Boolean getInGame() {
        return inGame;
    }

    public void setInGame(Boolean inGame) {
        this.inGame = inGame;
    }
}
