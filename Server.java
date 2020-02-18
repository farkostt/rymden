import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class Server {
    private ArrayList<ConnectionToClient> clientList;
    private LinkedBlockingQueue<String> messages;
    private ServerSocket serverSocket;

    public Server(int port, int players) throws IOException {
        clientList = new ArrayList<ConnectionToClient>();
        messages = new LinkedBlockingQueue<String>();
        serverSocket = new ServerSocket(port);

        String ipAddress = InetAddress.getLocalHost().getHostAddress();
        System.out.println("Server is running on IP-address: " + ipAddress + ", on port: " + port);

        Thread accept = new Thread() {
            public void run() {
                while (clientList.size() < players) {
                    try {
                        Socket s = serverSocket.accept();
                        clientList.add(new ConnectionToClient(s));
                        System.out.println("New user joined!");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        accept.setDaemon(true);
        accept.start();

        Thread messageHandling = new Thread() {
            public void run() {
                while (true) {
                    try {
                        String message = messages.take();
                        // Do some handling here...
                        System.out.println("Message Received by server: " + message);
                    } catch (InterruptedException e) {
                    }
                }
            }
        };

        messageHandling.setDaemon(true);
        messageHandling.start();
    }

    private class ConnectionToClient {
        PrintWriter writer;
        BufferedReader reader;
        Socket socket;

        ConnectionToClient(Socket socket) throws IOException {
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

            read.setDaemon(true); // terminate when main ends
            read.start();
        }

        public void write(String str) {
            writer.println(str);
        }
    }

    public void sendToOne(int index, String message) throws IndexOutOfBoundsException {
        clientList.get(index).write(message);
    }

    public void sendToAll(String message) {
        for (ConnectionToClient client : clientList)
            client.write(message);
    }

    public ArrayList<ConnectionToClient> getClients() {
        return clientList;
    }

}