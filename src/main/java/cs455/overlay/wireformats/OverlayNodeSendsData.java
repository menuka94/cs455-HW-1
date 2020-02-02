package cs455.overlay.wireformats;

public class OverlayNodeSendsData extends Event {

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
