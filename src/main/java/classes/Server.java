package classes;


import entities.Room;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private static final int PORT = 3456;
    private static List<Connection> connections = new ArrayList<>();
    private static List<Room> rooms = new ArrayList<>();
    private static final int ROOMS_COUNT = 5;

    static {
        init();
    }

    /*
    Создание комнат, карт (подгрузка).
     */
    public static void init() {
        for (int i = 0; i < ROOMS_COUNT; i++) {
            rooms.add(new Room(i));
        }
    }

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            int i = 0;
            while(true) {
                Socket s = serverSocket.accept();
                i++;
                connections.add(new Connection(s, connections, i, rooms));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
