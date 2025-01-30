import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class Server {
    private final String name;

    private ServerSocket serverSocket = null;

    // We use an int for the storage of each server and we implement thread-safety using intrinsic locks.
    private int storage;
    private final Object lock = new Object();

    /**
     * The constructor starts the server.
     *
     * @param port the port that the server listens.
     */
    public Server(String name, int port) {
        this.name = name;

        // init storage
        storage = new Random().nextInt(1000) + 1; //random value between [1,1000]

        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println(name + " could not listen on port: " + port);
            System.exit(1);
        }

        System.out.println(name + " started");
        accept();
    }

    /**
     * This method initiates the accept process for the server and processes the requests from clients.
     * It should be noted that both the consumers and producers use the same port,
     * with negative or positive values respectively.
     * Another implementation could use different ports for consumers and producers.
     */
    private void accept() {
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                new ServerThread(clientSocket).start();
            } catch (IOException e) {
                System.err.println(name + "accept failed.");
                System.exit(1);
            }
        }
    }

    /**
     * The thread which deals with the connection of each client.
     */
    private class ServerThread extends Thread {
        private final Socket clientSocket;

        public ServerThread(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            // the server doesn't send any messages to the clients, so we only use the in stream.
            BufferedReader in = null;
            String inputLine = null;
            try {
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                // We only read once, so no need for a loop.
                inputLine = in.readLine();
                // the number from producer is positive and from the client negative.
                int number = Integer.parseInt(inputLine);
                if (number > 0) {
                    System.out.println(name + " P Value: " + inputLine);
                } else {
                    System.out.println(name + " C Value: " + inputLine);
                }

                // we use a lock in a block while accessing the storage.
                synchronized (lock) {
                    if (storage + number > 1000) {
                        System.out.println(name + " MAX VALUE LIMIT: " + storage);
                    } else if (storage + number < 1) {
                        System.out.println(name + " MIN VALUE LIMIT: " + storage);
                    } else {
                        storage += number;
                        System.out.println(name + " NEW VALUE: " + storage);
                    }
                }
            } catch (IOException e) {
                System.err.println(name + " IOException...");
            } catch (ArithmeticException e) {
                System.err.println(name + " Invalid data: " + inputLine);
            }

            // close the streams and the sockets
            try {
                if (in != null) {
                    in.close();
                }
                clientSocket.close();
            } catch (IOException e) {
                System.err.println(name + " Error when closing sockets");
            }
        }
    }
}