package cs455.overlay.wireformats;

import java.net.Socket;

public class NodeReportsOverlaySetupStatus extends Event {

    /**
     * byte: Message type (NODE_REPORTS_OVERLAY_SETUP_STATUS)
     * int: Success status; Assigned ID if successful, -1 in case of a failure
     * byte: Length of following "Information string" field
     * byte[^^]: Information string; ASCII charset
     */
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
