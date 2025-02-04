import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

/**
 * Η κλάση Server υλοποιεί έναν server που:
 * - Ακούει σε συγκεκριμένη πόρτα για συνδέσεις από πελάτες (producers ή consumers).
 * - Λαμβάνει αριθμητικά δεδομένα από τους clients.
 * - Διαχειρίζεται ένα αποθηκευτικό χώρο που ενημερώνεται από τα δεδομένα των clients.
 */
public class Server {
    private final String name;  // Το όνομα του Server
    private ServerSocket serverSocket = null;  // Το socket του Server

    // Αποθηκευτικός χώρος του server (με προστασία για πολυνηματική πρόσβαση)
    private int storage;
    private final Object lock = new Object(); // Χρησιμοποιείται για thread-safety

    //Εκκίνηση του server
    public Server(String name, int port) {
        this.name = name;
        this.storage = new Random().nextInt(1000) + 1; // Τυχαία αρχική τιμή στον αποθηκευτικό χώρο (μεταξύ 1 και 1000)

        try {
            serverSocket = new ServerSocket(port); // Δημιουργία του ServerSocket που ακούει στη συγκεκριμένη θύρα
            System.out.println(name + " -> Ο server ξεκίνησε στην πόρτα " + port);
            acceptConnections(); // Κλήση της μεθόδου που διαχειρίζεται τις εισερχόμενες συνδέσεις
        } catch (IOException e) {
            System.err.println(name + " -> Σφάλμα κατά το άνοιγμα της θύρας " + port);
            System.exit(1); // Αν αποτύχει, τερματίζεται η εφαρμογή
        }
    }

    /**
     * Αποδοχή συνδέσεων από πελάτες και δημιουργία νέων νημάτων για κάθε σύνδεση.
     * - Κάθε νέος πελάτης (Producer ή Consumer) εξυπηρετείται σε νέο νήμα (Thread).
     */
    private void acceptConnections() {
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept(); // Αναμονή για σύνδεση πελάτη
                new ServerThread(clientSocket).start(); // Δημιουργία νέου νήματος για τον πελάτη
            } catch (IOException e) {
                System.err.println(name + " -> Σφάλμα κατά την αποδοχή σύνδεσης.");
            }
        }
    }

    // κλάση που διαχειρίζεται κάθε σύνδεση πελάτη.
    private class ServerThread extends Thread {
        private final Socket clientSocket; // Το socket που συνδέει τον client με τον server

        // Κατασκευαστής του νήματος.
        public ServerThread(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
                String inputLine = in.readLine(); // Διαβάζει το μήνυμα από τον πελάτη
                if (inputLine != null) {
                    processInput(inputLine); // Επεξεργασία του μηνύματος
                }
            } catch (IOException e) {
                System.err.println(name + " -> Σφάλμα εισόδου/εξόδου.");
            } finally {
                try {
                    clientSocket.close(); // Κλείσιμο της σύνδεσης με τον πελάτη
                } catch (IOException e) {
                    System.err.println(name + " -> Σφάλμα κατά το κλείσιμο του socket.");
                }
            }
        }

        // Επεξεργασία της εισόδου από τον πελάτη.
        private void processInput(String inputLine) {
            int number;
            try {
                number = Integer.parseInt(inputLine); // Μετατροπή της εισόδου σε ακέραιο αριθμό
            } catch (NumberFormatException e) {
                System.err.println(name + " -> Μη έγκυρα δεδομένα: " + inputLine);
                return; // Αν η είσοδος δεν είναι αριθμός, αγνοείται
            }

            // Προσδιορίζουμε αν το μήνυμα προήλθε από Producer ή Consumer
            if (number > 0) {
                System.out.println(name + " -> Παραλήφθηκε από Producer: " + number);
            } else {
                System.out.println(name + " -> Παραλήφθηκε από Consumer: " + number);
            }

            synchronized (lock) {
                // Έλεγχος αν το νέο αποθηκευμένο ποσό υπερβαίνει το μέγιστο επιτρεπτό (1000)
                if (storage + number > 1000) {
                    System.out.println(name + " -> Υπέρβαση μέγιστης τιμής: " + storage);
                }
                // Έλεγχος αν το νέο αποθηκευμένο ποσό πέφτει κάτω από το ελάχιστο επιτρεπτό (1)
                else if (storage + number < 1) {
                    System.out.println(name + " -> Υπέρβαση ελάχιστης τιμής: " + storage);
                }
                // Αν η τιμή είναι αποδεκτή, ενημερώνουμε την αποθήκευση
                else {
                    storage += number;
                    System.out.println(name + " -> Νέα τιμή αποθήκευσης: " + storage);
                }
            }
        }
    }
}
