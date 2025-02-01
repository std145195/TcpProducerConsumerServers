import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;

public class Consumer {
    // Πόσες φορές ο client θα στείλει αριθμούς στον server
    private static final int REPEAT = 10;

    private final String name;
    private final String[] hosts;
    private final int[] ports;

    public Consumer(String name, String[] hosts, int[] ports) {
        this.name = name;

        if (hosts.length != ports.length) {
            throw new RuntimeException(name + " -> Η λίστα των hosts και των ports έχει διαφορετικά μεγέθη.");
        }

        this.hosts = hosts;
        this.ports = ports;

        startCommunication();
    }

    /**
     * Ξεκινά η επικοινωνία με τον server.
     */
    private void startCommunication() {
        Random random = new Random();

        for (int i = 0; i < REPEAT; i++) {
            try {
                // Αναμονή για κάποιο τυχαίο διάστημα [1-10] δευτερόλεπτα
                Thread.sleep(1000 * (random.nextInt(10) + 1));
            } catch (InterruptedException e) {
                System.err.println(name + " -> Διακοπή κατά την αναμονή.");
                Thread.currentThread().interrupt();
                return;
            }

            // Επιλογή τυχαίου server
            int serverIndex = random.nextInt(hosts.length);
            System.out.println(name + " -> Σύνδεση με " + hosts[serverIndex] + " στην πόρτα " + ports[serverIndex]);

            try (Socket socket = new Socket(hosts[serverIndex], ports[serverIndex]);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

                System.out.println(name + " -> Συνδέθηκε στον server (" + i + ")");

                // Γεννάται ένας τυχαίος αρνητικός αριθμός [-100, -10]
                int value = -(random.nextInt(91) + 10);

                // Αποστολή της τιμής στον server
                out.println(value);
                System.out.println(name + " -> Στάλθηκε: " + value);

            } catch (IOException e) {
                System.err.println(name + " -> Σφάλμα σύνδεσης (" + i + ")");
                return;
            }
        }
        System.out.println(name + " -> Ολοκληρώθηκε η επικοινωνία.");
    }
}