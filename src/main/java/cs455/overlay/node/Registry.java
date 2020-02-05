package cs455.overlay.node;

import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Random;
import cs455.overlay.transport.TCPConnection;
import cs455.overlay.transport.TCPConnectionsCache;
import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.util.Constants;
import cs455.overlay.util.InteractiveCommandParser;
import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.OverlayNodeSendsDeregistration;
import cs455.overlay.wireformats.OverlayNodeSendsRegistration;
import cs455.overlay.wireformats.Protocol;
import cs455.overlay.wireformats.RegistryReportsDeregistrationStatus;
import cs455.overlay.wireformats.RegistryReportsRegistrationStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Registry implements Node {
    private static final Logger logger = LogManager.getLogger(Registry.class);
    private int port;
    private InteractiveCommandParser commandParser;
    private TCPServerThread tcpServerThread;
    private HashMap<Integer, Socket> registeredNodes;
    private Random random;

    private Registry(int port) throws IOException {
        this.port = port;
        tcpServerThread = new TCPServerThread(port, this);
        tcpServerThread.start();
        commandParser = new InteractiveCommandParser(this);
        commandParser.start();
        registeredNodes = new HashMap<>();
        random = new Random();
    }

    public static void main(String[] args) throws IOException {
        int port = Integer.parseInt(args[0]);
        Registry registry = new Registry(port);
    }

    @Override
    public void onEvent(Event event) {
        int type = event.getType();
        logger.info("onEvent: event type: " + type);

        switch (type) {
            case Protocol.OVERLAY_NODE_SENDS_REGISTRATION:
                registerOverlayNode(event);
                break;
            case Protocol.OVERLAY_NODE_SENDS_DEREGISTRATION:
                deregisterOverlayNode(event);
                break;
            case Protocol.OVERLAY_NODE_SENDS_DATA:
                respondToOverlayNodeSendsData();
                break;
            case Protocol.NODE_REPORTS_OVERLAY_SETUP_STATUS:
                respondToNodeReportsOverlaySetupStatus();
                break;
            case Protocol.OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY:
                respondToOverlayNodeReportsTrafficSummary();
                break;
            case Protocol.OVERLAY_NODE_REPORTS_TASK_FINISHED:
                respondToOverlayNodeReportsTaskFinished();
                break;
            default:
                logger.warn("Unknown event type: " + type);
                break;
        }
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

    private void deregisterOverlayNode(Event event) {
        OverlayNodeSendsDeregistration overlayNodeSendsDeregistration =
                (OverlayNodeSendsDeregistration) event;
        Socket socket = overlayNodeSendsDeregistration.getSocket();
        int nodeId = overlayNodeSendsDeregistration.getNodeId();

        RegistryReportsDeregistrationStatus responseEvent = new RegistryReportsDeregistrationStatus();

        // IP address check
        byte[] deregistrationEventIpAddress = overlayNodeSendsDeregistration.getIpAddress();
        if (!Arrays.equals(deregistrationEventIpAddress,
                socket.getInetAddress().getAddress())) {
            // Checking if there is a mismatch in the address that is specified in the registration
            // request and the IP address of the request (the socket’s input stream).
            String infoString = "mismatch in the address in the registration " +
                    "request and the one in the request (the socket’s input stream)";
            logger.warn(infoString);
            responseEvent.setSuccessStatus(-1);
            responseEvent.setLengthOfInfoString((byte) infoString.getBytes().length);
            responseEvent.setInfoString(infoString);
        } else if (!TCPConnectionsCache.containsConnection(socket)) {
            String infoString = "Connection not found in TCPConnectionsCache";
            logger.warn(infoString);
            responseEvent.setSuccessStatus(-1);
            responseEvent.setLengthOfInfoString((byte) infoString.getBytes().length);
            responseEvent.setInfoString(infoString);
        } else if (!registeredNodes.containsKey(nodeId)) {
            logger.warn("Node ID (" + nodeId + ") not registered with the Registry");
        } else {
            // Everything is OK. Proceed to deregister the node
            registeredNodes.remove(socket);
            String infoString = "Deregistration request successful. " +
                    "The number of messaging nodes currently constituting the overlay " +
                    "is (" + (registeredNodes.size() - 1) + ")";
            responseEvent.setSuccessStatus(nodeId);
            responseEvent.setLengthOfInfoString((byte) infoString.getBytes().length);
            responseEvent.setInfoString(infoString);
        }

        TCPConnection tcpConnection = TCPConnectionsCache.getConnection(socket);
        try {
            tcpConnection.sendData(responseEvent.getBytes());
            TCPConnectionsCache.removeConnection(socket);
        } catch (IOException e) {
            logger.error(e.getStackTrace());
        }
    }

    private void registerOverlayNode(Event event) {
        OverlayNodeSendsRegistration overlayNodeSendsRegistration =
                (OverlayNodeSendsRegistration) event;
        logger.info("IP Address Length: " + overlayNodeSendsRegistration.getIpAddressLength());
        System.out.println("IP Address: " + new String(overlayNodeSendsRegistration.getIpAddress()));
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
            responseEvent.setInfoString(infoString);
            responseEvent.setLengthOfInfoString((byte) infoString.getBytes().length);
        } else if (TCPConnectionsCache.containsConnection(socket)) {
            if (registeredNodes.containsValue(socket)) {
                // checking if the node has already been registered
                logger.warn("Node already registered");
                responseEvent.setSuccessStatus(-1);
                String infoString = "Node already registered";
                responseEvent.setInfoString(infoString);
                responseEvent.setLengthOfInfoString((byte) infoString.getBytes().length);
            } else {
                // proceed to register the node
                randomNodeId = random.nextInt(Constants.MAX_NODES);
                logger.info("Generated ID for new node: " + randomNodeId);
                responseEvent.setSuccessStatus(randomNodeId);
                String infoString = "Registration request successful. " +
                        "The number of messaging nodes currently constituting the overlay " +
                        "is (" + (registeredNodes.size() + 1) + ")";
                responseEvent.setInfoString(infoString);
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
            registeredNodes.put(randomNodeId, socket);
        } catch (IOException e) {
            logger.error("Error sending ");
            logger.error(e.getStackTrace());
        }
    }

    public void setupOverlay(int tableSize) {
        Collection<Integer> nodeIds = registeredNodes.keySet();
        logger.info("Available Node IDs");
        for (Integer nodeId : nodeIds) {
            logger.info(nodeId);
        }
    }
}
