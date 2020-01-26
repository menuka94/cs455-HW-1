package cs455.overlay.routing;

public class RoutingTable {
    private RoutingEntry[] routingEntries;

    public RoutingTable(int entries) {
        routingEntries = new RoutingEntry[entries];
    }
}
