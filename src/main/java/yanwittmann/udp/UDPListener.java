package yanwittmann.udp;

/**
 * Add this listener to the UDPServer to process incoming messages.<br>
 * This class has been written by <a href="http://yanwittmann.de">Yan Wittmann</a>.
 *
 * @author Yan Wittmann
 */
public abstract class UDPListener {
    public abstract void receive(String msg, String host);
}
