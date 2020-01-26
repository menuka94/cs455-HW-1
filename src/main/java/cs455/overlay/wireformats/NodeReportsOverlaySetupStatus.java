package cs455.overlay.wireformats;

public class NodeReportsOverlaySetupStatus implements Event {
    @Override
    public byte[] getBytes() {
        return new byte[0];
    }

    @Override
    public int getType() {
        return Protocol.NODE_REPORTS_OVERLAY_SETUP_STATUS;
    }
}
