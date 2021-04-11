package yanwittmann.io;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A simple UDP server that can receive messages. It will send the identical received string back as a confirmation.<br>
 * This class has been written by <a href="http://yanwittmann.de">Yan Wittmann</a>.
 * @author Yan Wittmann
 */
public class UDPServer {

    private final ArrayList<UDPListener> listeners = new ArrayList<>();
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

    public void setTimeout(int timeout) throws SocketException {
        socket.setSoTimeout(timeout);
    }

    public void run(boolean confirmation) {
        new Thread(() -> {
            running = true;

            while (running) {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                try {
                    socket.receive(packet);
                } catch (IOException ignored) {
                    return;
                }

                InetAddress address = packet.getAddress();
                int port = packet.getPort();
                packet = new DatagramPacket(buf, buf.length, address, port);
                String received = new String(packet.getData(), 0, packet.getLength()).replace("\0", "");
                String host = packet.getAddress().toString().replace("/", "");
                listeners.forEach(listener -> listener.receive(received, host));
                if (confirmation)
                    try {
                        socket.send(packet);
                    } catch (IOException ignored) {
                        return;
                    }
                Arrays.fill(buf, (byte) 0);
            }
            socket.close();
        }).start();
    }

    public int getPort() {
        return socket.getPort();
    }

    public void addReceiveListener(UDPListener listener) {
        listeners.add(listener);
    }

    public void close() {
        running = false;
        socket.close();
    }
}
