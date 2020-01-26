package cs455.overlay.wireformats;

public class OverlayNodeSendsData implements Event {
    @Override
    public byte[] getBytes() {
        return new byte[0];
    }

    @Override
    public int getType() {
        return Protocol.OVERLAY_NODE_SENDS_DATA;
    }
}
