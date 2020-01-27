package cs455.overlay.transport;

import java.io.IOException;
import java.net.Socket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TCPConnection {
    private static final Logger logger = LogManager.getLogger(TCPConnection.class);
    private Socket socket;
    private TCPSender tcpSender;
    private TCPReceiverThread tcpReceiverThread;

    public TCPConnection(Socket socket) throws IOException {
        this.socket = socket;
        tcpSender = new TCPSender(socket);
    }

    public Socket getSocket() {
        return socket;
    }

    public synchronized void startTCPReceiverThread() throws IOException {
        if (tcpReceiverThread == null) {
            tcpReceiverThread = new TCPReceiverThread(socket);
        }
        tcpReceiverThread.start();
    }

    public void sendData(byte[] data) {
        try {
            tcpSender.sendData(data);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
