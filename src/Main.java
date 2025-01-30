
/**
 * This class start is a helper to start all servers, consumers and producers.
 */
public class Main {
    private static final int SERVERS = 2;
    private static final int CONSUMERS = 1;
    private static final int PRODUCERS = 1;

    public static void main(String[] args) {
        String[] hosts = new String[SERVERS];
        int[] ports = new int[SERVERS];

        // Init hosts and ports, and start the servers.
        for (int i = 0; i < SERVERS; i++) {
            hosts[i] = "127.0.0.1";
            ports[i] = 9000 + i;
            new ServerStarter("S" + i, ports[i]).start();
        }

        // Start the consumers.
        for (int i = 0; i < CONSUMERS; i++) {
            new ConsumerStarter("C" + i, hosts, ports).start();
        }

        // Start the producers.
        for (int i = 0; i < PRODUCERS; i++) {
            new ProducerStarter("P" + i, hosts, ports).start();
        }
    }

    /**
     * A thread that start a server to ta specific port.
     */
    private static class ServerStarter extends Thread {
        String name;
        int port;

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
     * A thread that starts a consumer and sends lists of available servers (hosts and ports).
     */
    private static class ConsumerStarter extends Thread {
        String name;
        String[] hosts;
        int[] ports;

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
     * A thread that starts a producer and sends lists of available servers (hosts and ports).
     */
    private static class ProducerStarter extends Thread {
        String name;
        String[] hosts;
        int[] ports;

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