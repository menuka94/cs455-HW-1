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
}
