package cs455.overlay.routing;

public class RoutingEntry {
    private int distance;
    private int nodeId;
    private String ipAddress;
    private int port;

    public RoutingEntry(int distance, int nodeId, String ipAddress, int port) {
        this.distance = distance;
        this.nodeId = nodeId;
        this.ipAddress = ipAddress;
        this.port = port;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public int getPort() {
        return port;
    }

    public int getDistance() {
        return distance;
    }

    public int getNodeId() {
        return nodeId;
    }

    @Override
    public String toString() {
        return "RoutingEntry{" +
                "distance=" + distance +
                ", nodeId=" + nodeId +
                ", ipAddress='" + ipAddress + '\'' +
                ", port=" + port +
                '}';
    }
}
