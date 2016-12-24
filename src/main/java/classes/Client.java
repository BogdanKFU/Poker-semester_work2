package classes;

import javax.smartcardio.Card;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private final static String HOST = "localhost";
    private final static int PORT = 3456;
    private String name = "";
    private int id_room = 0;
    private Card first_card;
    private Card second_card;

    public static void main(String[] args) {
        Socket socket;
        try {
            socket = new Socket(HOST, PORT);
            Scanner sc = new Scanner(System.in);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            System.out.println(in.readLine());
            String name = sc.nextLine();
            out.println(name);
            System.out.println(in.readLine());
            ClientMessagePrinter printer = new ClientMessagePrinter(in);
            printer.start();
            boolean flag = true;
            while (flag) {
                String str = sc.nextLine();
                if (str.equals("exit")) {
                    printer.interrupt();
                    System.out.println("Closing the connection...");
                    out.println(str);
                    System.out.println("classes.Connection closed successfully.");
                    flag = false;
                }
                else {
                    out.println(str);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}