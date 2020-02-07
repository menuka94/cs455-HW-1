package cs455.overlay.wireformats;

import cs455.overlay.util.Validator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class NodeReportsOverlaySetupStatus extends Event {
    private static final Logger logger = LogManager.getLogger(NodeReportsOverlaySetupStatus.class);
    private byte messageType;
    private int successStatus;
    private byte lengthOfInfoString;
    private byte[] infoString;

    /**
     * byte: Message type (NODE_REPORTS_OVERLAY_SETUP_STATUS)
     * int: Success status; Assigned ID if successful, -1 in case of a failure
     * byte: Length of following "Information string" field
     * byte[^^]: Information string; ASCII charset
     */
    public NodeReportsOverlaySetupStatus(byte[] marshalledBytes) throws IOException {
        ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

        messageType = din.readByte();
        Validator.validateEventType(messageType, Protocol.NODE_REPORTS_OVERLAY_SETUP_STATUS, logger);

        successStatus = din.readInt();
        lengthOfInfoString = din.readByte();
        infoString = new byte[lengthOfInfoString];
        din.readFully(infoString, 0, lengthOfInfoString);

        baInputStream.close();
        din.close();
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
