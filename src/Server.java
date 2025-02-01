import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class Server {
    private final String name;
    private ServerSocket serverSocket = null;

    // Αποθηκευτικός χώρος του server (με thread-safety μέσω intrinsic lock)
    private int storage;
    private final Object lock = new Object();

    /**
     * Εκκίνηση του server.
     */
    public Server(String name, int port) {
        this.name = name;
        this.storage = new Random().nextInt(1000) + 1; // Τυχαία αρχική τιμή [1, 1000]

        try {
            serverSocket = new ServerSocket(port);
            System.out.println(name + " -> Ο server ξεκίνησε στην πόρτα " + port);
            acceptConnections();
        } catch (IOException e) {
            System.err.println(name + " -> Σφάλμα κατά το άνοιγμα της θύρας " + port);
            System.exit(1);
        }
    }

    /**
     * Αποδοχή συνδέσεων από πελάτες και δημιουργία νέων νημάτων για κάθε σύνδεση.
     */
    private void acceptConnections() {
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                new ServerThread(clientSocket).start();
            } catch (IOException e) {
                System.err.println(name + " -> Σφάλμα κατά την αποδοχή σύνδεσης.");
            }
        }
    }

    /**
     * Κλάση για την επεξεργασία κάθε σύνδεσης πελάτη.
     */
    private class ServerThread extends Thread {
        private final Socket clientSocket;

        public ServerThread(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
                String inputLine = in.readLine();
                if (inputLine != null) {
                    processInput(inputLine);
                }
            } catch (IOException e) {
                System.err.println(name + " -> Σφάλμα εισόδου/εξόδου.");
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    System.err.println(name + " -> Σφάλμα κατά το κλείσιμο του socket.");
                }
            }
        }

        /**
         * Επεξεργασία της εισόδου από τον πελάτη.
         */
        private void processInput(String inputLine) {
            int number;
            try {
                number = Integer.parseInt(inputLine);
            } catch (NumberFormatException e) {
                System.err.println(name + " -> Μη έγκυρα δεδομένα: " + inputLine);
                return;
            }

            if (number > 0) {
                System.out.println(name + " -> Παραλήφθηκε από Producer: " + number);
            } else {
                System.out.println(name + " -> Παραλήφθηκε από Consumer: " + number);
            }

            synchronized (lock) {
                if (storage + number > 1000) {
                    System.out.println(name + " -> Υπέρβαση μέγιστης τιμής: " + storage);
                } else if (storage + number < 1) {
                    System.out.println(name + " -> Υπέρβαση ελάχιστης τιμής: " + storage);
                } else {
                    storage += number;
                    System.out.println(name + " -> Νέα τιμή αποθήκευσης: " + storage);
                }
            }
        }
    }
}