package cs455.overlay.node;

import java.io.IOException;
import java.net.Socket;
import cs455.overlay.transport.TCPConnection;
import cs455.overlay.wireformats.Event;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MessagingNode implements Node {
    private static final Logger logger = LogManager.getLogger(MessagingNode.class);

    public static void main(String[] args) throws IOException {
        logger.info("Messaging node starting ...");
        Socket socket = new Socket("localhost", 5600);
        TCPConnection tcpConnection = new TCPConnection(socket);
        tcpConnection.sendData("Hello World".getBytes());
        tcpConnection.startTCPReceiverThread();
    }

    @Override
    public void onEvent(Event event) {

    }
}
