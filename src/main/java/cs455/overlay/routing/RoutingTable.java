package cs455.overlay.routing;

import java.util.ArrayList;
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

    public void print() {
        System.out.format("%-15s%-15s%-15s%-15s\n", new String[]{
                "NodeID", "Distance", "IP Address", "Port"});
        for (RoutingEntry routingEntry : routingEntries) {
            String[] row = new String[]{
                    String.valueOf(routingEntry.getNodeId()),
                    String.valueOf(routingEntry.getDistance()),
                    routingEntry.getIpAddress(),
                    String.valueOf(routingEntry.getPort())
            };
            System.out.format("%-15s%-15s%-15s%-15s\n", row);
        }
    }

}
