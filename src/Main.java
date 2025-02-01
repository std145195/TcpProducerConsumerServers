/**
 * Η κλάση Main εκκινεί τους servers, τους consumers και τους producers.
 */
public class Main {
    private static final int SERVERS = 2;
    private static final int CONSUMERS = 1;
    private static final int PRODUCERS = 1;

    public static void main(String[] args) {
        String[] hosts = new String[SERVERS];
        int[] ports = new int[SERVERS];

        // Αρχικοποίηση hosts και ports, και εκκίνηση των servers
        for (int i = 0; i < SERVERS; i++) {
            hosts[i] = "127.0.0.1";
            ports[i] = 9000 + i;
            new ServerStarter("S" + i, ports[i]).start();
        }

        // Δίνουμε λίγο χρόνο στους servers να εκκινήσουν
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            System.err.println("Σφάλμα κατά την αναμονή εκκίνησης των servers.");
            Thread.currentThread().interrupt();
        }

        // Εκκίνηση των consumers
        for (int i = 0; i < CONSUMERS; i++) {
            new ConsumerStarter("C" + i, hosts, ports).start();
        }

        // Εκκίνηση των producers
        for (int i = 0; i < PRODUCERS; i++) {
            new ProducerStarter("P" + i, hosts, ports).start();
        }
    }

    /**
     * Νήμα που εκκινεί έναν server σε συγκεκριμένη πόρτα.
     */
    private static class ServerStarter extends Thread {
        private final String name;
        private final int port;

        public ServerStarter(String name, int port) {
            this.name = name;
            this.port = port;
        }

        @Override
        public void run() {
            new Server(name, port);
        }
    }

    /**
     * Νήμα που εκκινεί έναν consumer και του στέλνει λίστες με τους διαθέσιμους servers (hosts και ports).
     */
    private static class ConsumerStarter extends Thread {
        private final String name;
        private final String[] hosts;
        private final int[] ports;

        public ConsumerStarter(String name, String[] hosts, int[] ports) {
            this.name = name;
            this.hosts = hosts;
            this.ports = ports;
        }

        @Override
        public void run() {
            new Consumer(name, hosts, ports);
        }
    }

    /**
     * Νήμα που εκκινεί έναν producer και του στέλνει λίστες με τους διαθέσιμους servers (hosts και ports).
     */
    private static class ProducerStarter extends Thread {
        private final String name;
        private final String[] hosts;
        private final int[] ports;

        public ProducerStarter(String name, String[] hosts, int[] ports) {
            this.name = name;
            this.hosts = hosts;
            this.ports = ports;
        }

        @Override
        public void run() {
            new Producer(name, hosts, ports);
        }
    }
}