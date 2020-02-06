package cs455.overlay.node;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;
import cs455.overlay.routing.RoutingEntry;
import cs455.overlay.routing.RoutingTable;
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
import cs455.overlay.wireformats.RegistrySendsNodeManifest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Registry implements Node {
    private static final Logger logger = LogManager.getLogger(Registry.class);
    private int port;
    private InteractiveCommandParser commandParser;
    private TCPServerThread tcpServerThread;
    private HashMap<Integer, Socket> registeredNodeSocketMap;
    private Random random;
    private HashMap<Integer, RoutingTable> routingTables;

    private Registry(int port) throws IOException {
        this.port = port;
        tcpServerThread = new TCPServerThread(port, this);
        tcpServerThread.start();
        commandParser = new InteractiveCommandParser(this);
        commandParser.start();
        registeredNodeSocketMap = new HashMap<>();
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
        } else if (!registeredNodeSocketMap.containsKey(nodeId)) {
            logger.warn("Node ID (" + nodeId + ") not registered with the Registry");
        } else {
            // Everything is OK. Proceed to deregister the node
            registeredNodeSocketMap.remove(socket);
            String infoString = "Deregistration request successful. " +
                    "The number of messaging nodes currently constituting the overlay " +
                    "is (" + (registeredNodeSocketMap.size() - 1) + ")";
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
            if (registeredNodeSocketMap.containsValue(socket)) {
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
                        "is (" + (registeredNodeSocketMap.size() + 1) + ")";
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
            registeredNodeSocketMap.put(randomNodeId, socket);
        } catch (IOException e) {
            logger.error("Error sending ");
            logger.error(e.getStackTrace());
        }
    }

    public void setupOverlay(final int tableSize) {
        routingTables = new HashMap<>();
        Set<Integer> nodeIdsSet = registeredNodeSocketMap.keySet();
        ArrayList<Integer> sortedNodeIds = new ArrayList<>(nodeIdsSet);
        Collections.sort(sortedNodeIds);  // sort the NodeIDs in ascending order
        int noOfRegisteredNodes = registeredNodeSocketMap.size();
        for (int i = 0; i < sortedNodeIds.size(); i++) {
            // ID of the node to which the current routing table should be sent
            Integer nodeIdToSendRoutingTable = sortedNodeIds.get(i);
            // create a routing table to send to the current node
            RoutingTable routingTable = new RoutingTable(tableSize);
            for (int j = 0; j < tableSize; j++) {
                // add routing entries to each routing table
                int distance = (int) Math.pow(2, j);    // distance is a power of 2
                int nodePosition = (distance + i) % noOfRegisteredNodes;
                int nodeId = sortedNodeIds.get(nodePosition);
                Socket socket = registeredNodeSocketMap.get(nodeId);

                RoutingEntry routingEntry = new RoutingEntry(distance, nodeId,
                        socket.getInetAddress().getHostAddress(), socket.getPort());
                routingTable.addRoutingEntry(routingEntry);
            }
            sendRoutingTable(routingTable, registeredNodeSocketMap.get(nodeIdToSendRoutingTable),
                    nodeIdToSendRoutingTable);
        }
    }

    private void sendRoutingTable(RoutingTable routingTable, Socket socket, int nodeId) {
        RegistrySendsNodeManifest event = new RegistrySendsNodeManifest();
        System.out.println("----------------------------------\n");
        System.out.println("Routing Table for Node " + nodeId);
        routingTable.print();

        // TCPConnection tcpConnection = TCPConnectionsCache.getConnection(socket);
        // try {
        //     tcpConnection.sendData(event.getBytes());
        // } catch (IOException e) {
        //     e.printStackTrace();
        // }
    }
}
