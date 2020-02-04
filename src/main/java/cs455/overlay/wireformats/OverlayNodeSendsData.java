package cs455.overlay.wireformats;

public class OverlayNodeSendsData extends Event {

    /**
     * byte: Message type; OVERLAY_NODE_SENDS_DATA
     * int: Destination ID
     * int: Source ID
     * int: Payload
     * int: Dissemination trace field length (number of hops)
     * int[^^]: Dissemination trace comprising nodeIDs that the packet traversed
     * through
     */
    public OverlayNodeSendsData(byte[] marshalledBytes) {

    }

    @Override
    public byte[] getBytes() {
        return new byte[0];
    }

    @Override
    public int getType() {
        return Protocol.OVERLAY_NODE_SENDS_DATA;
    }
}
