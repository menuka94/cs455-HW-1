package cs455.overlay.transport;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TCPSender {
    private static final Logger logger = LogManager.getLogger(TCPSender.class);
    private Socket socket;
    private DataOutputStream dout;

    public TCPSender(Socket socket) throws IOException {
        this.socket = socket;
        dout = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
    }

    public void sendData(byte[] dataToSend) throws IOException {
        synchronized (socket) {
            int dataLength = dataToSend.length;
            dout.writeInt(dataLength);
            dout.write(dataToSend, 0, dataLength);
            dout.flush();
        }
    }
}
