package cs455.overlay.wireformats;

public class OverlayNodeReportsTrafficSummary extends Event {
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
