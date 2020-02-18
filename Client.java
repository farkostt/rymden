import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.LinkedBlockingQueue;

public class Client {
    private ConnectionToServer server;
    private LinkedBlockingQueue<String> messages;
    private Socket socket;
    private String username;

    public Client(String IPAddress, int port, String username) {
        try {
            socket = new Socket(IPAddress, port);
            messages = new LinkedBlockingQueue<String>();
            server = new ConnectionToServer(socket);

        } catch (ConnectException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            System.out.println(e);
        } catch (IOException e) {
            System.out.println(e);
        }

        Thread messageHandling = new Thread() {
            public void run() {
                while (true) {
                    try {
                        String message = messages.take();
                        System.out.println("Message Received by " + username + ": " + message);
                    } catch (InterruptedException e) {
                    }
                }
            }
        };

        messageHandling.setDaemon(true);
        messageHandling.start();
    }

    private class ConnectionToServer {
        PrintWriter writer;
        BufferedReader reader;
        Socket socket;

        ConnectionToServer(Socket socket) throws IOException {
            this.socket = socket;

            writer = new PrintWriter(socket.getOutputStream(), true);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            Thread read = new Thread() {
                public void run() {
                    while (true) {
                        try {
                            String str = reader.readLine();
                            messages.put(str);
                        } catch (IOException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };

            read.setDaemon(true);
            read.start();
        }

        private void write(String str) {
            writer.println(str);
        }
    }

    public void send(String str) {
        server.write(str);
    }

    public String getUsername() {
        return username;
    }
}