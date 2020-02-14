package cs455.overlay.routing;

import java.util.ArrayList;
import java.util.Arrays;

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

    public int getNextBestNode(OverlayNodeSendsData sendsDataEvent, int[] allNodeIds) {
        int bestNodeToSendData = -1;
        int sourceId = sendsDataEvent.getSourceId();
        int destinationId = sendsDataEvent.getDestinationId();
        int noOfHops;

        int sourceIdIndex = Arrays.binarySearch(allNodeIds, sourceId);
        int destinationIdIndex = Arrays.binarySearch(allNodeIds, destinationId);
        int bestNodeDistance = -1;


        if (destinationIdIndex < sourceIdIndex) {
            noOfHops = allNodeIds.length - sourceIdIndex + destinationIdIndex;
        } else { // destinationIdIndex > sourceIdIndex
            noOfHops = destinationIdIndex - sourceIdIndex;
        }

        for (RoutingEntry routingEntry : routingEntries) {
            if (routingEntry.getDistance() < noOfHops &&
                    routingEntry.getDistance() > bestNodeDistance) {
                bestNodeDistance = routingEntry.getDistance();
            }
        }

        if (bestNodeDistance == -1) {
            logger.info("Error in choosing the best node");
            return -1;
        } else {
            int bestNodeIndex = -1;
            for (RoutingEntry r : routingEntries) {
                if (r.getDistance() == bestNodeDistance) {
                    bestNodeIndex = Arrays.binarySearch(allNodeIds, r.getNodeId());
                    break;
                }
            }
            bestNodeToSendData = allNodeIds[bestNodeIndex];

        }

        return bestNodeToSendData;
    }
}
