package cs455.overlay.wireformats;

public class OverlayNodeSendsDeregistration extends Event {

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
