/**
 * Η κλάση Main εκκινεί τους Servers, τους Consumers και τους Producers.
 * - Δημιουργεί και εκκινεί έναν αριθμό από Servers που ακούν σε συγκεκριμένες θύρες.
 * - Δημιουργεί και εκκινεί Consumers που λαμβάνουν δεδομένα από τους Servers.
 * - Δημιουργεί και εκκινεί Producers που στέλνουν δεδομένα στους Servers.
 */
public class Main {
    // Ο αριθμός των Servers που θα δημιουργηθούν
    private static final int SERVERS = 5;
    // Ο αριθμός των Consumers που θα δημιουργηθούν
    private static final int CONSUMERS = 2;
    // Ο αριθμός των Producers που θα δημιουργηθούν
    private static final int PRODUCERS = 2;

    public static void main(String[] args) {
        // Πίνακες για την αποθήκευση των διευθύνσεων (hosts) και των θυρών (ports) των servers
        String[] hosts = new String[SERVERS];
        int[] ports = new int[SERVERS];

        // Αρχικοποίηση των διευθύνσεων και των θυρών των Servers
        for (int i = 0; i < SERVERS; i++) {
            hosts[i] = "127.0.0.1"; // Όλοι οι servers τρέχουν τοπικά
            ports[i] = 9000 + i;    // Κάθε server θα ακούει σε διαφορετική θύρα (9000, 9001, ..., 9004)

            // Δημιουργία και εκκίνηση Server σε ξεχωριστό νήμα
            new ServerStarter("Server" + i, ports[i]).start();
        }

        // Αναμονή για να διασφαλιστεί ότι οι Servers έχουν ξεκινήσει πριν οι Clients επιχειρήσουν σύνδεση
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            System.err.println("Σφάλμα κατά την αναμονή εκκίνησης των servers.");
            Thread.currentThread().interrupt(); // Αν γίνει διακοπή, διατηρούμε τη σημαία interrupted
        }

        // Εκκίνηση των Consumers
        for (int i = 0; i < CONSUMERS; i++) {
            new ConsumerStarter("Consumer" + i, hosts, ports).start();
        }

        // Εκκίνηση των Producers
        for (int i = 0; i < PRODUCERS; i++) {
            new ProducerStarter("Producer" + i, hosts, ports).start();
        }
    }

    // Νήμα που εκκινεί έναν Server σε συγκεκριμένη πόρτα.
    private static class ServerStarter extends Thread {
        private final String name;
        private final int port;

        // Κατασκευαστής για τον ServerStarter.
        public ServerStarter(String name, int port) {
            this.name = name;
            this.port = port;
        }

        @Override
        public void run() {
            // Εκκίνηση ενός Server με το συγκεκριμένο όνομα και πόρτα
            new Server(name, port);
        }
    }

    // Νήμα που εκκινεί έναν Consumer.
    private static class ConsumerStarter extends Thread {
        private final String name;
        private final String[] hosts;
        private final int[] ports;

        // Κατασκευαστής για τον ConsumerStarter.
        public ConsumerStarter(String name, String[] hosts, int[] ports) {
            this.name = name;
            this.hosts = hosts;
            this.ports = ports;
        }

        @Override
        public void run() {
            // Δημιουργία ενός νέου Consumer που θα επικοινωνεί με τους Servers
            new Consumer(name, hosts, ports);
        }
    }

    // Νήμα που εκκινεί έναν Producer.
    private static class ProducerStarter extends Thread {
        private final String name;
        private final String[] hosts;
        private final int[] ports;

        // Κατασκευαστής για τον ProducerStarter.
        public ProducerStarter(String name, String[] hosts, int[] ports) {
            this.name = name;
            this.hosts = hosts;
            this.ports = ports;
        }

        @Override
        public void run() {
            // Δημιουργία ενός νέου Producer που θα επικοινωνεί με τους Servers
            new Producer(name, hosts, ports);
        }
    }
}
