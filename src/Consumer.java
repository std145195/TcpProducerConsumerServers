import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;

public class Consumer {
    // Ορίζει πόσες φορές θα στείλει δεδομένα στον server
    private static final int REPEAT = 10;

    private final String name;    // Όνομα του consumer (π.χ. "Consumer0")
    private final String[] hosts; // Λίστα διευθύνσεων IP των servers
    private final int[] ports;    // Λίστα θυρών στις οποίες ακούνε οι servers

    //Κατασκευαστής του Consumer.
    public Consumer(String name, String[] hosts, int[] ports) {
        this.name = name;

        // Έλεγχος ότι οι πίνακες hosts και ports έχουν το ίδιο μέγεθος
        if (hosts.length != ports.length) {
            throw new RuntimeException(name + " -> Η λίστα των hosts και των ports έχει διαφορετικά μεγέθη.");
        }

        this.hosts = hosts;
        this.ports = ports;

        startCommunication(); // Έναρξη επικοινωνίας με servers
    }

    /**
     * Η startCommunication διαχειρίζεται την επικοινωνία με τους servers.
     * - Επιλέγει έναν server τυχαία.
     * - Συνδέεται και στέλνει έναν τυχαίο αρνητικό αριθμό.
     * - Επαναλαμβάνει τη διαδικασία για REPEAT φορές.
     */
    private void startCommunication() {
        Random random = new Random();

        for (int i = 0; i < REPEAT; i++) {
            try {
                // Τυχαία καθυστέρηση [1, 10] δευτερόλεπτα μεταξύ αποστολών
                Thread.sleep(1000 * (random.nextInt(10) + 1));
            } catch (InterruptedException e) {
                System.err.println(name + " -> Διακοπή κατά την αναμονή.");
                Thread.currentThread().interrupt();
                return; // Αν το νήμα διακοπεί, τερματίζει την αποστολή
            }

            // Επιλογή ενός τυχαίου server από τις διαθέσιμες διευθύνσεις και θύρες
            int serverIndex = random.nextInt(hosts.length);
            System.out.println(name + " -> Σύνδεση με " + hosts[serverIndex] + " στην πόρτα " + ports[serverIndex]);

            // Προσπάθεια δημιουργίας σύνδεσης με τον επιλεγμένο server
            try (Socket socket = new Socket(hosts[serverIndex], ports[serverIndex]);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

                System.out.println(name + " -> Συνδέθηκε στον server (" + i + ")");

                // Δημιουργία τυχαίου αρνητικού αριθμού μεταξύ [-100, -10]
                int value = -(random.nextInt(91) + 10);

                // Αποστολή της τιμής στον server
                out.println(value);
                System.out.println(name + " -> Στάλθηκε " + value + " στον Server " + hosts[serverIndex] + ":" + ports[serverIndex]);

            } catch (IOException e) {
                System.err.println(name + " -> Σφάλμα σύνδεσης (" + i + ")");
                return; // Αν αποτύχει η σύνδεση, ο consumer σταματάει
            }
        }

        System.out.println(name + " -> Ολοκληρώθηκε η επικοινωνία.");
    }
}
