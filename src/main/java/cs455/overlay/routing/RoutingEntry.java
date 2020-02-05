package cs455.overlay.routing;

public class RoutingEntry {
    private int distance;
    private int nodeId;

    public RoutingEntry(int distance, int nodeId) {
        this.distance = distance;
        this.nodeId = nodeId;
    }


    public int getDistance() {
        return distance;
    }

    public int getNodeId() {
        return nodeId;
    }

    @Override
    public String toString() {
        return nodeId + "\t\t" + distance;
    }
}
