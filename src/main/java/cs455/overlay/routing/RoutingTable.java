package cs455.overlay.routing;

public class RoutingTable {
    private RoutingEntry[] routingEntries;
    private final int size;

    public RoutingTable(int size) {
        this.size = size;
        routingEntries = new RoutingEntry[size];
    }

    public RoutingEntry[] getRoutingEntries() {
        return routingEntries;
    }

    public int getSize() {
        return size;
    }
}
