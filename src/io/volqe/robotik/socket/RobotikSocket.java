import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {
        startServer(2303);
    }

    private static void startServer(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server läuft auf Port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client verbunden: " + clientSocket.getInetAddress());

                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            throw new RuntimeException("Fehler im Server", e);
        }
    }

    private static void handleClient(Socket clientSocket) {
        try (InputStream in = clientSocket.getInputStream();
             OutputStream out = clientSocket.getOutputStream()) {
            while (true) {
                int data = in.read();
                if (data == -1) { // Verbindung geschlossen
                    System.out.println("Client hat die Verbindung geschlossen.");
                    break;
                }
                if (data == 0x1) { // Ping empfangen
                    System.out.println("PING empfangen vom Client.");
                    out.write(0x1); // Pong zurücksenden
                } else {
                    System.out.println("Unbekannte Nachricht: " + data);
                }
            }
        } catch (IOException e) {
            System.err.println("Fehler bei der Kommunikation mit dem Client: " + e.getMessage());
        }
    }
}
