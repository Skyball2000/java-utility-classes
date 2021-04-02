package yanwittmann.io;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;

/**
 * A simple UDP server that can receive messages. It will send the identical received string back as a confirmation.<br>
 * This class has been written by <a href="http://yanwittmann.de">Yan Wittmann</a>.
 */
public class UDPServer {

    private final DatagramSocket socket;
    private final byte[] buf;
    private boolean running;

    public UDPServer(int port) throws SocketException {
        socket = new DatagramSocket(port);
        buf = new byte[256];
    }

    public UDPServer(int port, int bufferSize) throws SocketException {
        socket = new DatagramSocket(port);
        buf = new byte[bufferSize];
    }

    public void run() throws IOException {
        running = true;

        while (running) {
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);

            InetAddress address = packet.getAddress();
            int port = packet.getPort();
            packet = new DatagramPacket(buf, buf.length, address, port);
            String received = new String(packet.getData(), 0, packet.getLength());
            listeners.forEach(listener -> listener.receive(received));
            socket.send(packet);
        }
        socket.close();
    }

    ArrayList<UDPListener> listeners = new ArrayList<>();

    public void addReceiveListener(UDPListener listener) {
        listeners.add(listener);
    }

    public void close() {
        running = false;
    }
}
