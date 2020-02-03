package cs455.overlay.node;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

import cs455.overlay.transport.TCPConnection;
import cs455.overlay.transport.TCPConnectionsCache;
import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.util.InteractiveCommandParser;
import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.OverlayNodeSendsRegistration;
import cs455.overlay.wireformats.Protocol;
import cs455.overlay.wireformats.RegistryReportsRegistrationStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MessagingNode implements Node {
    private static final Logger logger = LogManager.getLogger(MessagingNode.class);

    private TCPConnection registryConnection;
    private TCPServerThread tcpServerThread;
    private InteractiveCommandParser commandParser;
    private int id; // randomly generated by the registry
    private LinkedBlockingQueue<Event> eventQueue;

    public MessagingNode(Socket registrySocket) throws IOException {
        registryConnection = new TCPConnection(registrySocket, this);

        tcpServerThread = new TCPServerThread(0, this);
        tcpServerThread.start();

        commandParser = new InteractiveCommandParser(this);
        commandParser.start();

        eventQueue = new LinkedBlockingQueue<>();
        MessagingNodeEventHandlerThread eventHandlerThread = new MessagingNodeEventHandlerThread();
        eventHandlerThread.start();

        sendRegistrationRequestToRegistry();
    }

    private void handleRegistryReportsRegistrationStatus(Event event) {
        RegistryReportsRegistrationStatus registrationStatus = (RegistryReportsRegistrationStatus) event;
        int successStatus = registrationStatus.getSuccessStatus();
        if (successStatus == -1) {
            logger.info("Registration failed!");
            logger.info(registrationStatus.getInfoString());
            System.exit(-1);
        } else {
            logger.info("Registration successful!");
            setId(successStatus);
            logger.info(registrationStatus.getInfoString());
        }
    }

    public static void main(String[] args) throws IOException {

        if (args.length < 2) {
            logger.error("Not enough arguments to start messaging node. Please provide registryHost and registryPort.");
            System.exit(1);
        } else if (args.length > 2) {
            logger.error("Too many arguments. Only the registryHost and registryPort are needed.");
            System.exit(1);
        }

        // Input is OK. Create a new messaging node
        String registryHost = args[0];
        int registryPort = Integer.parseInt(args[1]);
        Socket socket = new Socket(registryHost, registryPort);
        MessagingNode node = new MessagingNode(socket);
        TCPConnection connection;
        if (TCPConnectionsCache.containsConnection(socket)) {
            connection = TCPConnectionsCache.getConnection(socket);
            logger.info("Connection found in TCPConnectionsCache");
        } else {
            logger.info("Connection not found in TCPConnectionsCache. " +
                    "Creating a new connection");
            connection = new TCPConnection(socket, node);
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private class MessagingNodeEventHandlerThread extends Thread {
        @Override
        public void run() {
            try {
                Event event = eventQueue.take();
                int type = event.getType();

                switch (type) {
                    case Protocol.REGISTRY_REPORTS_REGISTRATION_STATUS:
                        handleRegistryReportsRegistrationStatus(event);
                        break;
                    case Protocol.REGISTRY_REPORTS_DEREGISTRATION_STATUS:
                        handleRegistryReportsDeregistrationStatus(event);
                        break;
                    case Protocol.REGISTRY_SENDS_NODE_MANIFEST:
                        handleRegistrySendsNodeManifest(event);
                        break;
                    case Protocol.REGISTRY_REQUESTS_TASK_INITIATE:
                        initiateTask(event);
                        break;
                    case Protocol.REGISTRY_REQUESTS_TRAFFIC_SUMMARY:
                        sendTaskSummaryToRegistry(event);
                        break;
                    default:
                        logger.error("Unknown event type" + type);
                }
            } catch (InterruptedException e) {
                logger.info(e.getStackTrace());
            }
        }
    }

    @Override
    public void onEvent(Event event) {
        eventQueue.offer(event);
    }

    private void sendTaskSummaryToRegistry(Event event) {

    }

    private void initiateTask(Event event) {

    }

    private void handleRegistrySendsNodeManifest(Event event) {

    }

    private void handleRegistryReportsDeregistrationStatus(Event event) {

    }

    /**
     * byte: Message Type (OVERLAY_NODE_SENDS_REGISTRATION)
     * byte: length of following "IP address" field
     * byte[^^]: IP address; from InetAddress.getAddress()
     * int: Port number
     */
    private void sendRegistrationRequestToRegistry() throws IOException {
        logger.info("sendRegistrationRequestToRegistry()");
        OverlayNodeSendsRegistration message = new OverlayNodeSendsRegistration();
        message.setIpAddressLength((byte) registryConnection.getSocket().
                getLocalAddress().getAddress().length);
        message.setIpAddress(registryConnection.getSocket().
                getLocalAddress().getAddress());
        message.setPort(tcpServerThread.getListeningPort());
        if (registryConnection.getSocket() == null) {
            logger.info("Registry socket is null");
        } else {
            logger.info("Registry socket is NOT null");
        }
        message.setSocket(registryConnection.getSocket());

        registryConnection.sendData(message.getBytes());
    }
}
