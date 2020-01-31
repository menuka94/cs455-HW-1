package cs455.overlay.node;

import java.io.IOException;
import java.net.Socket;

import cs455.overlay.transport.TCPConnection;
import cs455.overlay.transport.TCPConnectionsCache;
import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.OverlayNodeSendsRegistration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MessagingNode implements Node {
    private TCPConnection registryConnection;
    private TCPServerThread tcpServerThread;
    private TCPConnectionsCache tcpConnectionsCache;
    private static final Logger logger = LogManager.getLogger(MessagingNode.class);

    public MessagingNode(Socket registrySocket) throws IOException {
        registryConnection = new TCPConnection(registrySocket);

        sendRegistrationRequestToRegistry();
    }

    public static void main(String[] args) throws IOException {
        String registryHost = args[0];
        int registryPort = Integer.parseInt(args[1]);

        if (args.length < 2) {
            logger.error("Not enough arguments to start messaging node. Please provide registryHost and registryPort.");
            System.exit(1);
        } else if (args.length > 2) {
            logger.error("Too many arguments. Only the registryHost and registryPort are needed.");
            System.exit(1);
        }

        // Input is OK. Create a new messaging node
        Socket socket = new Socket(registryHost, registryPort);
        MessagingNode node = new MessagingNode(socket);
        TCPConnection connection = new TCPConnection(socket);
        connection.sendData("Hello World".getBytes());
        connection.startTCPReceiverThread();

    }

    @Override
    public void onEvent(Event event) {

    }

    private void sendRegistrationRequestToRegistry() {
        OverlayNodeSendsRegistration message = new OverlayNodeSendsRegistration();
    }
}
