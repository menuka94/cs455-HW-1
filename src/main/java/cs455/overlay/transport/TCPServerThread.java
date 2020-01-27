package cs455.overlay.transport;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TCPServerThread extends Thread {
    private static final Logger logger = LogManager.getLogger(TCPServerThread.class);

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(4511);
            serverSocket.accept();
            System.out.println("Connection Established");
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

}
