import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;

public class Consumer {
    // This parameter represents how many times the client will send numbers to the server.
    private static final int REPEAT = 10;

    private final String name;

    // The hosts of the servers
    public final String[] hosts;

    // The ports of the servers
    public final int[] ports;

    public Consumer(String name, String[] hosts, int[] ports) {
        this.name = name;

        if (hosts.length != ports.length) {
            throw new RuntimeException(name + " Server hosts and ports lists have different sizes.");
        }

        this.hosts = hosts;
        this.ports = ports;

        startCommunication();
    }

    private void startCommunication() {
        for (int i = 0; i < REPEAT; i++) {
            try {
                // Sleep for a while...
                Thread.sleep(1000 * (new Random().nextInt(10) + 1));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Socket socket = null;

            // pick a random server.
            int serverIndex = new Random().nextInt(hosts.length);
            System.out.println(name + " Attempting to connect to host " + hosts[serverIndex] + " on port " + ports[serverIndex]);
            try {
                socket = new Socket(hosts[serverIndex], ports[serverIndex]);
                System.out.println(name + " connected " + i);
            } catch (IOException e) {
                System.err.println(name + " connection error " + i);
                System.exit(1);
            }

            // The consumer only writes.
            PrintWriter out = null;
            try {
                out = new PrintWriter(socket.getOutputStream(), true);
            } catch (IOException e) {
                System.err.println(name + " Couldn't get I/O");
                System.exit(1);
            }

            int value = new Random().nextInt(91) + 10; // [10, 100]
            value -= 2 * value; //negative value for consumers

            // send the value to server.
            out.println(value);

            // close the streams and the sockets
            try {
                out.close();
                socket.close();
            } catch (IOException e) {
                System.err.println(name + " Error when closing sockets");
            }
        }
        System.out.println(name + " finished");
    }
}