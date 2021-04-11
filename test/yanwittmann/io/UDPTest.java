package yanwittmann.io;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import yanwittmann.utils.Sleep;

import java.io.IOException;

public class UDPTest {

    @Test
    public void sendAndReceiveTest() throws IOException {
        UDPServer server = new UDPServer(23435);
        UDPClient client = new UDPClient("192.168.2.22", 23435);
        server.addReceiveListener(new UDPListener() {
            @Override
            public void receive(String msg, String host) {
                System.out.println(host + " " + msg);
                Assertions.assertEquals(msg, "message");
            }
        });
        try {
            server.run(false);
        } catch (Exception ignored) {
        }
        client.setTimeout(1000);
        try {
            client.send("message", false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Sleep.milliseconds(400);
        server.close();
        client.close();
    }
}
