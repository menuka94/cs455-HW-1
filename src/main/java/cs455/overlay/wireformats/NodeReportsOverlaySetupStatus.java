package cs455.overlay.wireformats;

import java.net.Socket;

public class NodeReportsOverlaySetupStatus extends Event {
    public NodeReportsOverlaySetupStatus(byte[] marshalledBytes) {

    }


    @Override
    public byte[] getBytes() {
        return new byte[0];
    }

    @Override
    public int getType() {
        return Protocol.NODE_REPORTS_OVERLAY_SETUP_STATUS;
    }
}
