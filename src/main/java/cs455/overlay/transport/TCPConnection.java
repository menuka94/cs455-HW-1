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
        tcpReceiverThread = new TCPReceiverThread(socket);
        tcpReceiverThread.start();
    }

    public Socket getSocket() {
        return socket;
    }

    public void sendData(byte[] data) throws IOException {
        if (tcpSender == null) {
            tcpSender = new TCPSender(socket);
        }
        try {
            tcpSender.sendData(data);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    public String getDestinationAddress() {
        return socket.getInetAddress().getHostAddress();
    }

    public int getDestinationPort() {
        return socket.getPort();
    }

    public byte[] getLocalAddress() {
        return socket.getLocalAddress().getAddress();
    }

    public int getLocalPort() {
        return socket.getLocalPort();
    }

    @Override
    public String toString() {
        return "TCPConnection{" +
                "destinationAddress=" + getDestinationAddress() +
                ", destinationPort=" + getDestinationPort() +
                ", localAddress=" + getLocalAddress() +
                ", localPort=" + getLocalPort() +
                '}';
    }
}
