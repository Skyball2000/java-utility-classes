package yanwittmann.io;

/**
 * Add this listener to the UDPServer to process incoming messages.<br>
 * This class has been written by <a href="http://yanwittmann.de">Yan Wittmann</a>.
 */
public abstract class UDPListener {
    public abstract void receive(String msg);
}
