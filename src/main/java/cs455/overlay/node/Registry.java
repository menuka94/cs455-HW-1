package cs455.overlay.node;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import cs455.overlay.routing.RoutingEntry;
import cs455.overlay.routing.RoutingTable;
import cs455.overlay.transport.TCPConnection;
import cs455.overlay.transport.TCPConnectionsCache;
import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.util.Constants;
import cs455.overlay.util.InteractiveCommandParser;
import cs455.overlay.util.StatisticsCollectorAndDisplay;
import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.NodeReportsOverlaySetupStatus;
import cs455.overlay.wireformats.OverlayNodeReportsTaskFinished;
import cs455.overlay.wireformats.OverlayNodeReportsTrafficSummary;
import cs455.overlay.wireformats.OverlayNodeSendsDeregistration;
import cs455.overlay.wireformats.OverlayNodeSendsRegistration;
import cs455.overlay.wireformats.Protocol;
import cs455.overlay.wireformats.RegistryReportsDeregistrationStatus;
import cs455.overlay.wireformats.RegistryReportsRegistrationStatus;
import cs455.overlay.wireformats.RegistryRequestsTaskInitiate;
import cs455.overlay.wireformats.RegistryRequestsTrafficSummary;
import cs455.overlay.wireformats.RegistrySendsNodeManifest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Registry implements Node {
    private static final Logger logger = LogManager.getLogger(Registry.class);
    private int port;
    private static boolean overlaySetup = false;
    private InteractiveCommandParser commandParser;
    private TCPServerThread tcpServerThread;
    private volatile HashMap<Integer, Socket> registeredNodeSocketMap;
    private Random random;
    private volatile ConcurrentHashMap<Integer, RoutingTable> routingTables;
    private volatile ConcurrentHashMap<Integer, Integer> registeredNodeListeningPortMap;
    private volatile int noOfConfirmedOverlayNodes = 0;
    private volatile int noOfTaskFinishedNodes = 0;
    private volatile int noOfSummaryReportedNodes = 0;

    private StatisticsCollectorAndDisplay statisticsCollector;

    private TCPConnectionsCache tcpConnectionsCache;

    private Registry(int port) throws IOException {
        this.port = port;
        tcpConnectionsCache = new TCPConnectionsCache();
        tcpServerThread = new TCPServerThread(port, this, tcpConnectionsCache);
        commandParser = new InteractiveCommandParser(this);
        registeredNodeSocketMap = new HashMap<>();
        registeredNodeListeningPortMap = new ConcurrentHashMap<>();
        random = new Random();
    }

    public static void main(String[] args) throws IOException {
        int port = Integer.parseInt(args[0]);
        Registry registry = new Registry(port);
        registry.tcpServerThread.start();
        registry.commandParser.start();
    }

    @Override
    public void onEvent(Event event) {
        int type = event.getType();
        logger.debug("onEvent: event type: " + type);

        switch (type) {
            case Protocol.OVERLAY_NODE_SENDS_REGISTRATION:
                registerOverlayNode(event);
                break;
            case Protocol.OVERLAY_NODE_SENDS_DEREGISTRATION:
                deregisterOverlayNode(event);
                break;
            case Protocol.NODE_REPORTS_OVERLAY_SETUP_STATUS:
                respondToNodeReportsOverlaySetupStatus(event);
                break;
            case Protocol.OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY:
                respondToOverlayNodeReportsTrafficSummary(event);
                break;
            case Protocol.OVERLAY_NODE_REPORTS_TASK_FINISHED:
                respondToOverlayNodeReportsTaskFinished(event);
                break;
            default:
                logger.warn("Unknown event type: " + type);
        }
    }

    public void start(int noOfPacketsToSend) {
        if (overlaySetup) {
            requestTaskInitiate(noOfPacketsToSend);
        } else {
            logger.warn("Overlay has not been set up. Cannot start sending messages.");
        }
    }

    private void requestTaskInitiate(int noOfPacketsToSend) {
        RegistryRequestsTaskInitiate taskInitiateEvent = new RegistryRequestsTaskInitiate();
        taskInitiateEvent.setNoOfPacketsToSend(noOfPacketsToSend);

        Collection<Socket> allSockets = registeredNodeSocketMap.values();
        for (Socket socket : allSockets) {
            TCPConnection tcpConnection = tcpConnectionsCache.getConnection(socket);
            try {
                tcpConnection.sendData(taskInitiateEvent.getBytes());
            } catch (IOException e) {
                logger.error(e.getStackTrace());
            }
        }
    }

    public void listMessagingNodes() {
        ArrayList<Integer> nodeIds = new ArrayList<>(registeredNodeSocketMap.keySet());
        System.out.println("No. of Nodes: " + nodeIds.size());
        Collections.sort(nodeIds);
        for (int i = 0; i < nodeIds.size(); i++) {
            Integer nodeId = nodeIds.get(i);
            Socket socket = registeredNodeSocketMap.get(nodeId);
            System.out.println("Node ID: " + nodeId +
                    ", IP Address: " + socket.getInetAddress().getHostAddress() +
                    ", Port: " + socket.getPort()
            );
        }
    }

    private synchronized void respondToOverlayNodeReportsTaskFinished(Event event) {
        OverlayNodeReportsTaskFinished taskFinishedEvent = (OverlayNodeReportsTaskFinished) event;
        int receivedNodeId = taskFinishedEvent.getNodeId();

        if (registeredNodeSocketMap.containsKey(receivedNodeId)) {
            noOfTaskFinishedNodes++;
        } else {
            logger.warn("Node " + receivedNodeId + " is not registered.");
        }

        logger.info("Waiting for all nodes to finish sending messages.");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            logger.error(e.getStackTrace());
        }

        logger.debug("Waiting complete");
        logger.debug("noOfTaskFinishedNodes: " + noOfTaskFinishedNodes);
        logger.debug("noOfConfirmedOverlayNodes: " + noOfConfirmedOverlayNodes);
        if (noOfTaskFinishedNodes == noOfConfirmedOverlayNodes) {
            // ready to request traffic summary
            logger.debug("All nodes have finished sending data.");
            RegistryRequestsTrafficSummary requestsTrafficSummaryEvent =
                    new RegistryRequestsTrafficSummary();

            statisticsCollector = new StatisticsCollectorAndDisplay(noOfTaskFinishedNodes);

            for (Socket socket : registeredNodeSocketMap.values()) {
                TCPConnection tcpConnection = tcpConnectionsCache.getConnection(socket);
                try {
                    tcpConnection.sendData(requestsTrafficSummaryEvent.getBytes());
                } catch (IOException e) {
                    logger.error(e.getStackTrace());
                }
            }
        }
    }

    private synchronized void respondToOverlayNodeReportsTrafficSummary(Event event) {
        OverlayNodeReportsTrafficSummary trafficSummaryEvent =
                (OverlayNodeReportsTrafficSummary) event;

        noOfSummaryReportedNodes++;

        statisticsCollector.add(trafficSummaryEvent);

        if (noOfSummaryReportedNodes == noOfTaskFinishedNodes) {
            // clear counters for next iteration
            noOfTaskFinishedNodes = 0;
            noOfSummaryReportedNodes = 0;
        }
    }

    private void respondToNodeReportsOverlaySetupStatus(Event event) {
        NodeReportsOverlaySetupStatus overlaySetupStatusEvent = (NodeReportsOverlaySetupStatus) event;

        int successStatus = overlaySetupStatusEvent.getSuccessStatus();
        if (successStatus == -1) {
            logger.warn(overlaySetupStatusEvent.getInfoString());
        } else if (registeredNodeSocketMap.containsKey(successStatus)) {
            // successful
            logger.info(overlaySetupStatusEvent.getInfoString());
            synchronized (this) {
                noOfConfirmedOverlayNodes++;
            }
        } else {
            logger.warn("Node " + successStatus + " not found in registered messaging " +
                    "nodes");
        }

        if (noOfConfirmedOverlayNodes == registeredNodeSocketMap.size()) {
            logger.info("Registry now ready to initiate tasks.");
        }
    }


    private synchronized void deregisterOverlayNode(Event event) {
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
        } else if (!tcpConnectionsCache.containsConnection(socket)) {
            String infoString = "Connection not found in TCPConnectionsCache";
            logger.warn(infoString);
            responseEvent.setSuccessStatus(-1);
            responseEvent.setLengthOfInfoString((byte) infoString.getBytes().length);
            responseEvent.setInfoString(infoString);
        } else if (!registeredNodeSocketMap.containsKey(nodeId)) {
            logger.warn("Node ID (" + nodeId + ") not registered with the Registry");
        } else {
            // Everything is OK. Proceed to deregister the node
            registeredNodeSocketMap.remove(nodeId, socket);
            String infoString = "Deregistration request successful. " +
                    "The number of messaging nodes currently constituting the overlay " +
                    "is (" + (registeredNodeSocketMap.size() - 1) + ")";
            responseEvent.setSuccessStatus(nodeId);
            responseEvent.setLengthOfInfoString((byte) infoString.getBytes().length);
            responseEvent.setInfoString(infoString);
        }

        TCPConnection tcpConnection = tcpConnectionsCache.getConnection(socket);
        try {
            tcpConnection.sendData(responseEvent.getBytes());
            tcpConnectionsCache.removeConnection(socket);
        } catch (IOException e) {
            logger.error(e.getStackTrace());
        }
    }

    private synchronized void registerOverlayNode(Event event) {
        OverlayNodeSendsRegistration overlayNodeSendsRegistration =
                (OverlayNodeSendsRegistration) event;
        logger.debug("IP Address Length: " + overlayNodeSendsRegistration.getIpAddressLength());
        logger.debug("IP Address: " + new String(overlayNodeSendsRegistration.getIpAddress()));
        logger.debug("Port: " + overlayNodeSendsRegistration.getPort());
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
        } else if (tcpConnectionsCache.containsConnection(socket)) {
            if (registeredNodeSocketMap.containsValue(socket)) {
                // checking if the node has already been registered
                logger.warn("Node already registered");
                responseEvent.setSuccessStatus(-1);
                String infoString = "Node already registered";
                responseEvent.setInfoString(infoString);
                responseEvent.setLengthOfInfoString((byte) infoString.getBytes().length);
            } else {
                // proceed to register the node
                randomNodeId = random.nextInt(Constants.MAX_NODES) + 1; // add one to avoid zero

                // check if the ID has already been assigned
                Set<Integer> assignedIds = registeredNodeSocketMap.keySet();
                while (assignedIds.contains(randomNodeId)) {
                    randomNodeId = random.nextInt(Constants.MAX_NODES) + 1; // add one to avoid zero
                }

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

        TCPConnection tcpConnection = tcpConnectionsCache.getConnection(socket);
        try {
            tcpConnection.sendData(responseEvent.getBytes());
            registeredNodeSocketMap.put(randomNodeId, socket);
            registeredNodeListeningPortMap.put(randomNodeId,
                    overlayNodeSendsRegistration.getPort());
        } catch (IOException e) {
            logger.error("Error sending ");
            logger.error(e.getStackTrace());
        }
    }

    public void setupOverlay(final int tableSize) {
        routingTables = new ConcurrentHashMap<>();
        Set<Integer> nodeIdsSet = registeredNodeSocketMap.keySet();
        ArrayList<Integer> sortedNodeIds = new ArrayList<>(nodeIdsSet);
        Collections.sort(sortedNodeIds);  // sort the NodeIDs in ascending order
        int noOfRegisteredNodes = registeredNodeSocketMap.size();
        for (int i = 0; i < sortedNodeIds.size(); i++) {
            // ID of the node to which the current routing table should be sent
            Integer nodeIdToSendRoutingTable = sortedNodeIds.get(i);

            int[] nodeIdsToSend = new int[tableSize];
            byte[] ipAddressLengthsToSend = new byte[tableSize];
            byte[][] ipAddressesToSend = new byte[tableSize][];
            int[] portsToSend = new int[tableSize];

            // create a routing table to send to the current node
            RoutingTable routingTable = new RoutingTable(tableSize);
            for (int j = 0; j < tableSize; j++) {
                // add routing entries to the current routing table
                int distance = (int) Math.pow(2, j);    // distance is a power of 2
                int nodePosition = (distance + i) % noOfRegisteredNodes;
                int nodeId = sortedNodeIds.get(nodePosition);
                Socket socket = registeredNodeSocketMap.get(nodeId);

                nodeIdsToSend[j] = nodeId;
                ipAddressesToSend[j] = socket.getInetAddress().getHostAddress().getBytes();
                ipAddressLengthsToSend[j] = (byte) ipAddressesToSend[j].length;
                portsToSend[j] = registeredNodeListeningPortMap.get(nodeId);

                RoutingEntry routingEntry = new RoutingEntry(distance, nodeId,
                        socket.getInetAddress().getHostAddress(), socket.getPort());
                routingTable.addRoutingEntry(routingEntry);
            }
            routingTables.put(nodeIdToSendRoutingTable, routingTable);

            RegistrySendsNodeManifest event = new RegistrySendsNodeManifest();
            event.setNodesIds(nodeIdsToSend);
            event.setIpAddresses(ipAddressesToSend);
            event.setIpAddressLengths(ipAddressLengthsToSend);
            event.setTableSize(tableSize);
            event.setPorts(portsToSend);

            event.setNoOfAllNodeIds(sortedNodeIds.size());
            event.setAllNodeIds(arrayListToArray(sortedNodeIds));

            sendRoutingTable(event, registeredNodeSocketMap.get(nodeIdToSendRoutingTable));
            overlaySetup = true;
        }
    }

    private int[] arrayListToArray(ArrayList<Integer> arrayList) {
        int[] array = new int[arrayList.size()];
        for (int i = 0; i < arrayList.size(); i++) {
            array[i] = arrayList.get(i);
        }

        return array;
    }

    private void sendRoutingTable(RegistrySendsNodeManifest event, Socket socket) {
        TCPConnection tcpConnection = tcpConnectionsCache.getConnection(socket);
        try {
            tcpConnection.sendData(event.getBytes());
        } catch (IOException e) {
            logger.error(e.getStackTrace());
        }
    }

    public void listRoutingTables() {
        if (overlaySetup) {
            Set<Integer> nodeIds = routingTables.keySet();
            for (Integer nodeId : nodeIds) {
                Socket socket = registeredNodeSocketMap.get(nodeId);
                System.out.println("\n\n\nNode: " + nodeId);
                System.out.println("IP Address: " + socket.getInetAddress().getHostAddress());
                System.out.println("Port: " + socket.getPort());

                RoutingTable routingTable = routingTables.get(nodeId);
                routingTable.printRoutingTable();
            }
        } else {
            logger.warn("Overlay setup has not started. No routing tables available");
        }
    }

}
