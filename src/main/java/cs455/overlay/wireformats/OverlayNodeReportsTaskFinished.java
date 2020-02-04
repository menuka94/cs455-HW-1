package cs455.overlay.wireformats;

public class OverlayNodeReportsTaskFinished extends Event {

    /**
     * byte: Message type; OVERLAY_NODE_REPORTS_TASK_FINISHED
     * byte: length of following "IP address" field
     * byte[^^]: Node IP address:
     * int: Node Port number:
     * int: nodeID
     */
    public OverlayNodeReportsTaskFinished(byte[] marshalledBytes) {

    }

    @Override
    public byte[] getBytes() {
        return new byte[0];
    }

    @Override
    public int getType() {
        return Protocol.OVERLAY_NODE_REPORTS_TASK_FINISHED;
    }
}
