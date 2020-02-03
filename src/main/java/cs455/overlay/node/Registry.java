package cs455.overlay.node;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;
import cs455.overlay.transport.TCPConnection;
import cs455.overlay.transport.TCPConnectionsCache;
import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.util.Constants;
import cs455.overlay.util.InteractiveCommandParser;
import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.OverlayNodeSendsRegistration;
import cs455.overlay.wireformats.Protocol;
import cs455.overlay.wireformats.RegistryReportsRegistrationStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Registry implements Node {
    private static final Logger logger = LogManager.getLogger(Registry.class);
    private int port;
    private InteractiveCommandParser commandParser;
    private TCPServerThread tcpServerThread;
    private LinkedBlockingQueue<Event> eventQueue;
    private HashMap<byte[], MessagingNode> registeredNodes;
    private HashMap<MessagingNode, Integer> registeredNodeIds;
    private Random random;

    private class RegistryEventHandlerThread extends Thread {
        private Logger logger = LogManager.getLogger(RegistryEventHandlerThread.class);

        @Override
        public void run() {
            while (true) {
                try {
                    Event event = eventQueue.take();

                    int type = event.getType();

                    if (type == Protocol.OVERLAY_NODE_SENDS_REGISTRATION) {
                        registerOverlayNode(event);
                    } else if (type == Protocol.OVERLAY_NODE_SENDS_DEREGISTRATION) {
                        deregisterOverlayNode();
                    } else if (type == Protocol.OVERLAY_NODE_SENDS_DATA) {
                        respondToOverlayNodeSendsData();
                    } else if (type == Protocol.NODE_REPORTS_OVERLAY_SETUP_STATUS) {
                        respondToNodeReportsOverlaySetupStatus();
                    } else if (type == Protocol.OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY) {
                        respondToOverlayNodeReportsTrafficSummary();
                    } else if (type == Protocol.OVERLAY_NODE_REPORTS_TASK_FINISHED) {
                        respondToOverlayNodeReportsTaskFinished();
                    } else {
                        logger.warn("Unknown event type: " + type);
                    }
                } catch (InterruptedException e) {
                    logger.error(e.getStackTrace());
                }
            }
        }
    }

    private Registry(int port) throws IOException {
        tcpServerThread = new TCPServerThread(port, this);
        tcpServerThread.start();
        commandParser = new InteractiveCommandParser(this);
        commandParser.start();
        eventQueue = new LinkedBlockingQueue<>();
        registeredNodes = new HashMap<>();
        registeredNodeIds = new HashMap<>();
        random = new Random();
        RegistryEventHandlerThread eventHandlerThread = new RegistryEventHandlerThread();
        eventHandlerThread.start();
    }

    public static void main(String[] args) throws IOException {
        logger.info("main()");
        int port = Integer.parseInt(args[0]);
        Registry registry = new Registry(port);
    }

    @Override
    public void onEvent(Event event) {
        boolean offer = eventQueue.offer(event);
    }

    public void listMessagingNodes() {
        TCPConnectionsCache.printConnections();
    }

    private void respondToOverlayNodeReportsTaskFinished() {

    }

    private void respondToOverlayNodeReportsTrafficSummary() {

    }

    private void respondToNodeReportsOverlaySetupStatus() {

    }

    private void respondToOverlayNodeSendsData() {

    }

    private void deregisterOverlayNode() {

    }

    private void registerOverlayNode(Event event) {
        logger.info("registerOverlayNode()");
        OverlayNodeSendsRegistration overlayNodeSendsRegistration = (OverlayNodeSendsRegistration) event;
        MessagingNode node = overlayNodeSendsRegistration.getNode();
        logger.info("IP Address Length: " + overlayNodeSendsRegistration.getIpAddressLength());
        System.out.println("IP Address: " +
                new String(overlayNodeSendsRegistration.getIpAddress(), StandardCharsets.UTF_8));
        System.out.println("Port: " + overlayNodeSendsRegistration.getPort());
        int randomNodeId = 0;

        RegistryReportsRegistrationStatus responseEvent = new RegistryReportsRegistrationStatus();

        byte[] registrationEventIpAddress = overlayNodeSendsRegistration.getIpAddress();
        Socket socket = overlayNodeSendsRegistration.getSocket();
        if (!Arrays.equals(registrationEventIpAddress,
                socket.getInetAddress().getAddress())) {
            // Checking if there is a mismatch in the address that is specified in the registration
            // request and the IP address of the request (the socket’s input stream).
            logger.warn("IP addresses differ");
            responseEvent.setSuccessStatus(-1);
            String infoString = "mismatch in the address in the registration " +
                    "request and the one in the request (the socket’s input stream)";
            responseEvent.setInfoString(infoString.getBytes());
            responseEvent.setLengthOfInfoString((byte) infoString.getBytes().length);
        } else if (TCPConnectionsCache.containsConnection(socket)) {
            if (registeredNodes.containsKey(socket.getInetAddress().getAddress()) ||
                    registeredNodes.containsValue(node)) {
                // checking if the node has already been registered
                logger.warn("Node already registered");
                responseEvent.setSuccessStatus(-1);
                String infoString = "Node already registered";
                responseEvent.setInfoString(infoString.getBytes());
                responseEvent.setLengthOfInfoString((byte) infoString.getBytes().length);
            } else {
                // proceed to register the node
                randomNodeId = random.nextInt(Constants.MAX_NODES);
                logger.info("Generated ID for new node: " + randomNodeId);
                node.setId(randomNodeId);
                responseEvent.setSuccessStatus(randomNodeId);
                String infoString = "Registration request successful. " +
                        "The number of messaging nodes currently constituting the overlay " +
                        "is (" + registeredNodes.size() + ")";
                responseEvent.setInfoString(infoString.getBytes());
                responseEvent.setLengthOfInfoString((byte) infoString.getBytes().length);
            }
        } else {
            // connection not found in TCPConnectionCache. Something is wrong.
            logger.error("Connection not found in TCPConnectionsCache. Please try again");
            return;
        }

        TCPConnection tcpConnection = TCPConnectionsCache.getConnection(socket);
        try {
            tcpConnection.sendData(responseEvent.getBytes());
            registeredNodes.put(socket.getInetAddress().getAddress(), node);
            if (randomNodeId != 0) {
                registeredNodeIds.put(node, randomNodeId);
            }
        } catch (IOException e) {
            logger.error("Error sending ");
            logger.error(e.getStackTrace());
        }
    }
}
