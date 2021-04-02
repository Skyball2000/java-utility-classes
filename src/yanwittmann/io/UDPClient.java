package yanwittmann.io;

import java.io.IOException;
import java.net.*;

/**
 * A simple UDP client that can send string messages to a host with an IP.<br>
 * This class has been written by <a href="http://yanwittmann.de">Yan Wittmann</a>.
 */
public class UDPClient {

    private final InetAddress address;
    private final DatagramSocket socket;
    private final int port;

    public UDPClient(String hostname, int port) throws UnknownHostException, SocketException {
        address = InetAddress.getByName(hostname);
        socket = new DatagramSocket();
        this.port = port;
    }

    public boolean send(String msg) throws IOException {
        byte[] buf = msg.getBytes();
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
        socket.send(packet);
        packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);
        return new String(packet.getData(), 0, packet.getLength()).equals(msg);
    }

    public void close() {
        socket.close();
    }
}
