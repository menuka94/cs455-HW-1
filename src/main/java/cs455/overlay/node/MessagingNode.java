package cs455.overlay.node;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import cs455.overlay.routing.RoutingEntry;
import cs455.overlay.routing.RoutingTable;
import cs455.overlay.transport.TCPConnection;
import cs455.overlay.transport.TCPConnectionsCache;
import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.util.InteractiveCommandParser;
import cs455.overlay.wireformats.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MessagingNode implements Node {
    private static final Logger logger = LogManager.getLogger(MessagingNode.class);

    private TCPConnection registryConnection;
    private TCPServerThread tcpServerThread;
    private InteractiveCommandParser commandParser;
    private int nodeId; // randomly generated by the registry

    public MessagingNode(Socket registrySocket) throws IOException {
        registryConnection = new TCPConnection(registrySocket, this);

        tcpServerThread = new TCPServerThread(0, this);
        tcpServerThread.start();

        commandParser = new InteractiveCommandParser(this);
        commandParser.start();

        sendRegistrationRequestToRegistry();
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

    public int getNodeId() {
        return nodeId;
    }

    public void setNodeId(int nodeId) {
        this.nodeId = nodeId;
    }

    @Override
    public void onEvent(Event event) {
        int type = event.getType();

        switch (type) {
            case Protocol.REGISTRY_REPORTS_REGISTRATION_STATUS:
                handleRegistryReportsRegistrationStatus(event);
                break;
            case Protocol.REGISTRY_REPORTS_DEREGISTRATION_STATUS:
                handleRegistryReportsDeregistrationStatus(event);
                break;
            case Protocol.REGISTRY_SENDS_NODE_MANIFEST:
                respondToRegistrySendsNodeManifest(event);
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
    }

    private void sendTaskSummaryToRegistry(Event event) {

    }

    private void initiateTask(Event event) {

    }

    /**
     * byte: Message type; REGISTRY_SENDS_NODE_MANIFEST
     * byte: routing table size N R
     * =============================================================================
     * int: Node ID of node 1 hop away
     * byte: length of following "IP address" field
     * byte[^^]: IP address of node 1 hop away; from InetAddress.getAddress()
     * int: Port number of node 1 hop away
     * -----------------------------------------------------------------------------
     * int: Node ID of node 2 hops away
     * byte: length of following "IP address" field
     * byte[^^]: IP address of node 2 hops away; from InetAddress.getAddress()
     * int: Port number of node 2 hops away
     * -----------------------------------------------------------------------------
     * int: Node ID of node 4 hops away
     * byte: length of following "IP address" field
     * byte[^^]: IP address of node 4 hops away; from InetAddress.getAddress()
     * int: Port number of node 4 hops away
     * =============================================================================
     * byte: Number of node IDs in the system
     * int[^^]: List of all node IDs in the system [Note no IPs are included]
     */
    private void respondToRegistrySendsNodeManifest(Event event) {
        RegistrySendsNodeManifest nodeManifestEvent = (RegistrySendsNodeManifest) event;
        int tableSize = nodeManifestEvent.getTableSize();
        logger.info("tableSize: " + tableSize);
        RoutingTable routingTable = new RoutingTable(tableSize);
        ArrayList<RoutingEntry> routingEntries = routingTable.getRoutingEntries();
        for (int i = 0; i < tableSize; i++) {
            routingTable.addRoutingEntry(new RoutingEntry(
                    (int) Math.pow(2, i),
                    nodeManifestEvent.getNodesIds()[i],
                    new String(nodeManifestEvent.getIpAddresses()[i]),
                    nodeManifestEvent.getPorts()[i]
            ));
            logger.info("IP Address received: " + new String(nodeManifestEvent.getIpAddresses()[i]));
        }
        logger.info("No. of Routing Entries: " + routingEntries.size());
        System.out.println("\n\nRouting Table of node " + nodeId);
        System.out.println("--------------------------------------");
        routingTable.printRoutingTable();
        System.out.println("--------------------------------------");

        connectToNodesInRoutingTable(routingTable);

        // prepare response event
        NodeReportsOverlaySetupStatus responseEvent = new NodeReportsOverlaySetupStatus();
        responseEvent.setSuccessStatus(getNodeId());
        String infoString = "Node " + getNodeId() + " successfully initiated connections with all" +
                " nodes in the routing table.";
        responseEvent.setLengthOfInfoString((byte) infoString.getBytes().length);
        responseEvent.setInfoString(infoString);

        try {
            registryConnection.sendData(responseEvent.getBytes());
        } catch (IOException e) {
            logger.error("Error sending data to Registry");
            logger.error(e.getStackTrace());
        }
    }

    private void connectToNodesInRoutingTable(RoutingTable routingTable) {
        ArrayList<RoutingEntry> routingEntries = routingTable.getRoutingEntries();
        for (RoutingEntry routingEntry : routingEntries) {
            System.out.println("\n\nConnecting to node: " + routingEntry.getNodeId());
            try {
                logger.info("IPAddress: " + routingEntry.getIpAddress());
                logger.info("Port: " + routingEntry.getPort());
                Socket socket = new Socket(routingEntry.getIpAddress(), routingEntry.getPort());
                TCPConnection tcpConnection = new TCPConnection(socket, this);
                TCPConnectionsCache.addConnection(socket, tcpConnection);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void handleRegistryReportsRegistrationStatus(Event event) {
        RegistryReportsRegistrationStatus registrationStatus =
                (RegistryReportsRegistrationStatus) event;
        int successStatus = registrationStatus.getSuccessStatus();
        if (successStatus == -1) {
            logger.info("Registration failed!");
            logger.info(registrationStatus.getInfoString());
            System.exit(-1);
        } else {
            logger.info("Registration successful!");
            setNodeId(successStatus);
            logger.info(registrationStatus.getInfoString());
        }
    }

    private void handleRegistryReportsDeregistrationStatus(Event event) {
        RegistryReportsDeregistrationStatus deregistrationStatus =
                (RegistryReportsDeregistrationStatus) event;
        int successStatus = deregistrationStatus.getSuccessStatus();
        if (successStatus == -1) {
            logger.info("Deregistration failed");
            logger.info(deregistrationStatus.getInfoString());
        } else if (successStatus == getNodeId()) {
            logger.info("Deregistration successful!");
            logger.info(deregistrationStatus.getInfoString());
            commandParser.stopAcceptingCommands();
            Socket socket = deregistrationStatus.getSocket();
            // TODO: properly stop commandParser thread
            commandParser.interrupt();
        } else {
            logger.warn("Deregistration failed. Reason unknown.");
        }
    }

    /**
     * byte: Message Type (OVERLAY_NODE_SENDS_REGISTRATION)
     * byte: length of following "IP address" field
     * byte[^^]: IP address; from InetAddress.getAddress()
     * int: Port number
     */
    private void sendRegistrationRequestToRegistry() throws IOException {
        OverlayNodeSendsRegistration message = new OverlayNodeSendsRegistration();
        message.setIpAddressLength((byte) registryConnection.getSocket().
                getLocalAddress().getAddress().length);
        message.setIpAddress(registryConnection.getSocket().
                getLocalAddress().getAddress());
        message.setPort(tcpServerThread.getListeningPort());
        message.setSocket(registryConnection.getSocket());

        registryConnection.sendData(message.getBytes());
    }

    /**
     * byte: Message Type (OVERLAY_NODE_SENDS_DEREGISTRATION)
     * byte: length of following "IP address" field
     * byte[^^]: IP address; from InetAddress.getAddress()
     * int: Port number
     */
    private void sendDeregistrationRequestToRegistry() throws IOException {
        OverlayNodeSendsDeregistration deregistrationEvent = new OverlayNodeSendsDeregistration();
        deregistrationEvent.setIpAddressLength((byte) registryConnection.getSocket().
                getLocalAddress().getAddress().length);
        deregistrationEvent.setIpAddress(registryConnection.getSocket().
                getLocalAddress().getAddress());
        deregistrationEvent.setPort(tcpServerThread.getListeningPort());
        deregistrationEvent.setSocket(registryConnection.getSocket());
        deregistrationEvent.setNodeId(getNodeId());

        registryConnection.sendData(deregistrationEvent.getBytes());
    }

    public void exitOverlay() throws IOException {
        sendDeregistrationRequestToRegistry();
    }
}
