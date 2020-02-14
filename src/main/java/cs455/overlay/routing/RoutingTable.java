package cs455.overlay.routing;

import java.util.ArrayList;

import cs455.overlay.wireformats.OverlayNodeSendsData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RoutingTable {
    private static final Logger logger = LogManager.getLogger(RoutingTable.class);
    private ArrayList<RoutingEntry> routingEntries;
    private final int tableSize;

    public RoutingTable(int tableSize) {
        this.tableSize = tableSize;
        routingEntries = new ArrayList<>();
    }

    public ArrayList<RoutingEntry> getRoutingEntries() {
        return routingEntries;
    }

    public void addRoutingEntry(RoutingEntry routingEntry) {
        if (routingEntries.size() < tableSize) {
            routingEntries.add(routingEntry);
        } else {
            logger.warn("Adding more entries will exceed routing table size (" + tableSize + ")");
        }
    }

    public int getTableSize() {
        return tableSize;
    }

    public void printRoutingTable() {
        System.out.printf("%-12s %-16s %-15s %s\n", "DISTANCE", "NODE ID", "IP", "PORT");

        ArrayList<RoutingEntry> routingEntries = getRoutingEntries();
        for (RoutingEntry routingEntry : routingEntries) {
            System.out.printf("%-12s %-16s %-15s %s\n",
                    routingEntry.getDistance(),
                    routingEntry.getNodeId(),
                    routingEntry.getIpAddress(),
                    routingEntry.getPort()
            );
        }
    }

    public boolean containsNodeId(int nodeId) {
        for (RoutingEntry routingEntry : routingEntries) {
            if (routingEntry.getNodeId() == nodeId) {
                return true;
            }
        }
        logger.warn("Node " + nodeId + " not found in the routing table");
        return false;
    }

    public RoutingEntry getRoutingEntry(int nodeId) {
        for (RoutingEntry routingEntry : routingEntries) {
            if (routingEntry.getNodeId() == nodeId) {
                return routingEntry;
            }
        }
        logger.warn("Unable to retrieve node " + nodeId + " from the routing table");
        return null;
    }

    public int findBestNodeToSendData(OverlayNodeSendsData sendsDataEvent, int[] allNodeIds) {
        int bestNodeToSendData = -1;
        int source = sendsDataEvent.getSourceId();
        int destination = sendsDataEvent.getDestinationId();

        int[] nodeIdsInRoutingTable = getNodeIdsInRoutingTable();

        if (destination > source) {
            for (int i = nodeIdsInRoutingTable.length - 1; i == 0; i--) {
                if (nodeIdsInRoutingTable[i] < destination) {
                    bestNodeToSendData = nodeIdsInRoutingTable[i];
                    break;
                }
            }
        } else {
            // destination < source: (cannot be equal here)
            for (int i = 0; i < nodeIdsInRoutingTable.length; i++) {

            }
        }

        return bestNodeToSendData;
    }

    public int[] getNodeIdsInRoutingTable() {
        int[] nodeIdsInRoutingTable = new int[tableSize];
        if (tableSize != routingEntries.size()) {
            logger.warn("Table size does not match the number of entries in the routing table.");
        }
        for (int i = 0; i < tableSize; i++) {
            nodeIdsInRoutingTable[i] = routingEntries.get(i).getNodeId();
        }

        return nodeIdsInRoutingTable;
    }
}
