package cs455.overlay.transport;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TCPReceiverThread extends Thread {
    private static final Logger logger = LogManager.getLogger(TCPReceiverThread.class);
    private Socket socket;
    private DataInputStream din;

    public TCPReceiverThread(Socket socket) throws IOException {
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
            } catch (IOException se) {
                logger.error(se.getMessage());
                break;
            }
        }
    }
}
