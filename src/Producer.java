import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;

public class Producer {
    // Αριθμός επαναλήψεων αποστολής δεδομένων στον server
    private static final int REPEAT = 10;

    private final String name;
    private final String[] hosts;
    private final int[] ports;

    public Producer(String name, String[] hosts, int[] ports) {
        this.name = name;

        if (hosts.length != ports.length) {
            throw new IllegalArgumentException(name + ": Τα hosts και τα ports έχουν διαφορετικό μέγεθος.");
        }

        this.hosts = hosts;
        this.ports = ports;

        startCommunication();
    }

    private void startCommunication() {
        Random random = new Random();

        for (int i = 0; i < REPEAT; i++) {
            try {
                // Τυχαία καθυστέρηση πριν την αποστολή
                Thread.sleep(1000 * (random.nextInt(10) + 1));
            } catch (InterruptedException e) {
                System.err.println(name + ": Διακοπή νήματος.");
                Thread.currentThread().interrupt();
                return;
            }

            int serverIndex = random.nextInt(hosts.length);
            String host = hosts[serverIndex];
            int port = ports[serverIndex];

            System.out.println(name + " -> Προσπάθεια σύνδεσης στον server " + host + " στην πόρτα " + port);

            try (Socket socket = new Socket(host, port);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

                System.out.println(name + " -> Συνδέθηκε με επιτυχία (" + i + ")");

                int value = random.nextInt(91) + 10; // [10, 100]
                out.println(value); // Αποστολή δεδομένων
                System.out.println(name + " -> Απεστάλη: " + value);

            } catch (IOException e) {
                System.err.println(name + " -> Σφάλμα σύνδεσης στον server (" + i + ")");
            }
        }

        System.out.println(name + " -> Τερματισμός αποστολής δεδομένων.");
    }
}