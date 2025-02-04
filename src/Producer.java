import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;

public class Producer {
    // Ορίζει πόσες φορές θα στείλει δεδομένα στον server
    private static final int REPEAT = 10;

    private final String name;    // Όνομα του producer
    private final String[] hosts; // Λίστα διευθύνσεων IP των servers
    private final int[] ports;    // Λίστα θυρών στις οποίες ακούνε οι servers

    // Κατασκευαστής του Producer.
    public Producer(String name, String[] hosts, int[] ports) {
        this.name = name;

        // Έλεγχος ότι οι πίνακες hosts και ports έχουν το ίδιο μέγεθος
        if (hosts.length != ports.length) {
            throw new IllegalArgumentException(name + ": Τα hosts και τα ports έχουν διαφορετικό μέγεθος.");
        }

        this.hosts = hosts;
        this.ports = ports;

        startCommunication(); // Έναρξη επικοινωνίας με servers
    }

    /**
     * Η startCommunication διαχειρίζεται την επικοινωνία με τους servers.
     * - Επιλέγει έναν server τυχαία.
     * - Συνδέεται και στέλνει έναν τυχαίο αριθμό.
     * - Επαναλαμβάνει τη διαδικασία για REPEAT φορές.
     */
    private void startCommunication() {
        Random random = new Random();

        for (int i = 0; i < REPEAT; i++) {
            try {
                // Τυχαία καθυστέρηση [1, 10] δευτερόλεπτα μεταξύ αποστολών
                Thread.sleep(1000 * (random.nextInt(10) + 1));
            } catch (InterruptedException e) {
                System.err.println(name + ": Διακοπή νήματος.");
                Thread.currentThread().interrupt();
                return; // Αν το νήμα διακοπεί, τερματίζει την αποστολή
            }

            // Επιλογή ενός τυχαίου server από τις διαθέσιμες διευθύνσεις και θύρες
            int serverIndex = random.nextInt(hosts.length);
            String host = hosts[serverIndex];
            int port = ports[serverIndex];

            System.out.println(name + " -> Προσπάθεια σύνδεσης στον server " + host + " στην πόρτα " + port);

            // Προσπάθεια δημιουργίας σύνδεσης με τον επιλεγμένο server
            try (Socket socket = new Socket(host, port);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

                System.out.println(name + " -> Συνδέθηκε με επιτυχία (" + i + ")");

                int value = random.nextInt(91) + 10; // Δημιουργία τυχαίου αριθμού μεταξύ [10, 100]
                out.println(value); // Αποστολή της τιμής στον server
                System.out.println(name + " -> Στάλθηκε " + value + " στον Server " + host + ":" + port);

            } catch (IOException e) {
                System.err.println(name + " -> Σφάλμα σύνδεσης στον server (" + i + ")");
            }
        }

        System.out.println(name + " -> Τερματισμός αποστολής δεδομένων.");
    }
}
