package classes;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.SocketException;

/*
Служит для получения и вывода сообщений от других игроков
Хранится у клиента
 */
public class ClientMessagePrinter extends Thread {

    private final BufferedReader in;

    public ClientMessagePrinter(BufferedReader in) {
        this.in = in;
    }

    public void run() {
        try {
            while (!this.isInterrupted()) {
                String str;
                try {
                    str = in.readLine();
                    System.out.println(str);
                } catch (SocketException e) {
                    return;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
