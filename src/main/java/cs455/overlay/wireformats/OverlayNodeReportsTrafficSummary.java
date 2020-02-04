package cs455.overlay.wireformats;

public class OverlayNodeReportsTrafficSummary extends Event {

    /**
     * byte: Message type; OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY
     * int: Assigned node ID
     * int: Total number of packets sent
     * (only the ones that were started/initiated by the node)
     * int: Total number of packets relayed
     * (received from a different node and forwarded)
     * long: Sum of packet data sent
     * (only the ones that were started by the node)
     * int: Total number of packets received
     * (packets with this node as final destination)
     * long: Sum of packet data received
     * (only packets that had this node as final destination)
     */
    public OverlayNodeReportsTrafficSummary(byte[] marshalledBytes) {

    }

    @Override
    public byte[] getBytes() {
        return new byte[0];
    }

    @Override
    public int getType() {
        return Protocol.OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY;
    }
}
