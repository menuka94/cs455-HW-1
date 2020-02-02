package cs455.overlay.transport;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import cs455.overlay.node.Node;
import cs455.overlay.util.Constants;
import cs455.overlay.wireformats.EventFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TCPReceiverThread extends Thread {
    private static final Logger logger = LogManager.getLogger(TCPReceiverThread.class);
    private Socket socket;
    private DataInputStream din;
    private Node node;

    public TCPReceiverThread(Socket socket, Node node) throws IOException {
        this.node = node;
        this.socket = socket;
        din = new DataInputStream(socket.getInputStream());
    }

    public void run() {
        int dataLength;
        while (socket != null) {
            try {
                dataLength = din.readInt();
                byte[] data = new byte[dataLength];
                din.readFully(data, 0, dataLength);
                // logger.info("readFully: " + data);
                node.onEvent(EventFactory.getInstance().getEvent(data));
            } catch (IOException se) {
                logger.error(se.getStackTrace());
                break;
            }
        }
    }
}
