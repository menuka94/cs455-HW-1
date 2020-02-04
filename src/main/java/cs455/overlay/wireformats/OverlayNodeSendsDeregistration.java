package cs455.overlay.wireformats;

public class OverlayNodeSendsDeregistration extends Event {

    /**
     * byte: Message Type (OVERLAY_NODE_SENDS_DEREGISTRATION)
     * byte: length of following "IP address" field
     * byte[^^]: IP address; from InetAddress.getAddress()
     * int: Port number
     * int: assigned Node ID
     */
    public OverlayNodeSendsDeregistration(byte[] marshalledBytes) {

    }

    @Override
    public byte[] getBytes() {
        return new byte[0];
    }

    @Override
    public int getType() {
        return Protocol.OVERLAY_NODE_SENDS_DEREGISTRATION;
    }
}
