package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OverlayNodeSendsData extends Event {
    private static final Logger logger = LogManager.getLogger(OverlayNodeSendsData.class);

    private byte messageType;
    private int destinationId;
    private int sourceId;
    private int payload;
    private int disseminationTraceLength;  // number of hops
    private int[] disseminationTrace;

    public OverlayNodeSendsData() {

    }

    /**
     * byte: Message type; OVERLAY_NODE_SENDS_DATA
     * int: Destination ID
     * int: Source ID
     * int: Payload
     * int: Dissemination trace field length (number of hops)
     * int[^^]: Dissemination trace comprising nodeIDs that the packet traversed
     * through
     */
    public OverlayNodeSendsData(byte[] marshalledBytes) throws IOException {
        ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

        messageType = din.readByte();

        destinationId = din.readInt();
        sourceId = din.readInt();
        payload = din.readInt();
        disseminationTraceLength = din.readInt();
        disseminationTrace = new int[disseminationTraceLength + 1];
        for (int i = 0; i < disseminationTraceLength; i++) {
            disseminationTrace[i] = din.readInt();
        }

        baInputStream.close();
        din.close();
    }

    @Override
    public byte[] getBytes() {
        byte[] marshalledBytes = null;
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

        try {
            dout.writeByte(getType());
            dout.writeInt(destinationId);
            dout.writeInt(sourceId);
            dout.writeInt(payload);
            dout.writeInt(disseminationTraceLength);
            for (int i = 0; i < disseminationTraceLength; i++) {
                dout.writeInt(disseminationTrace[i]);
            }

            dout.flush();

            marshalledBytes = baOutputStream.toByteArray();

        } catch (IOException e) {
            logger.error(e.getStackTrace());
        } finally {
            try {
                baOutputStream.close();
                dout.close();
            } catch (IOException e) {
                logger.error(e.getStackTrace());
            }
        }

        return marshalledBytes;
    }

    @Override
    public int getType() {
        return Protocol.OVERLAY_NODE_SENDS_DATA;
    }

    public byte getMessageType() {
        return messageType;
    }

    public int getDestinationId() {
        return destinationId;
    }

    public int getSourceId() {
        return sourceId;
    }

    public int getPayload() {
        return payload;
    }

    public int getDisseminationTraceLength() {
        return disseminationTraceLength;
    }

    public int[] getDisseminationTrace() {
        return disseminationTrace;
    }

    public void setDestinationId(int destinationId) {
        this.destinationId = destinationId;
    }

    public void setSourceId(int sourceId) {
        this.sourceId = sourceId;
    }

    public void setPayload(int payload) {
        this.payload = payload;
    }

    public void setDisseminationTraceLength(int disseminationTraceLength) {
        this.disseminationTraceLength = disseminationTraceLength;
    }

    public void setDisseminationTrace(int[] disseminationTrace) {
        this.disseminationTrace = disseminationTrace;
    }
}
