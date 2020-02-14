package cs455.overlay.wireformats;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OverlayNodeReportsTrafficSummary extends Event {
    private static final Logger logger = LogManager.getLogger(OverlayNodeReportsTrafficSummary.class);

    byte messageType;
    int nodeId;
    int numPacketsSent;
    int numPacketsRelayed;
    long sumPacketsSent;
    int numPacketsReceived;
    long sumPacketsReceived;

    public OverlayNodeReportsTrafficSummary() {

    }

    /**
     * byte: Message type; OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY
     * int: Assigned node ID
     * int: Total number of packets sent
     * (only the ones that were started/initiated by the node)
     * int: Total number of packets relayed
     * (received from a different node and forwarded)
     * long: Sum of packet data sent
     * (only the ones that were started by the node)
     * int: Total number of packets received
     * (packets with this node as final destination)
     * long: Sum of packet data received
     * (only packets that had this node as final destination)
     */
    public OverlayNodeReportsTrafficSummary(byte[] marshalledBytes) throws IOException {
        ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(baInputStream);

        messageType = din.readByte();

        nodeId = din.readInt();
        numPacketsSent = din.readInt();
        numPacketsRelayed = din.readInt();
        sumPacketsSent = din.readLong();
        numPacketsReceived = din.readInt();
        sumPacketsReceived = din.readLong();

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
            dout.writeInt(nodeId);
            dout.writeInt(numPacketsSent);
            dout.writeInt(numPacketsRelayed);
            dout.writeLong(sumPacketsSent);
            dout.writeInt(numPacketsReceived);
            dout.writeLong(sumPacketsReceived);

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
        return Protocol.OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY;
    }

    public void setMessageType(byte messageType) {
        this.messageType = messageType;
    }

    public int getNodeId() {
        return nodeId;
    }

    public void setNodeId(int nodeId) {
        this.nodeId = nodeId;
    }

    public int getNumPacketsSent() {
        return numPacketsSent;
    }

    public void setNumPacketsSent(int numPacketsSent) {
        this.numPacketsSent = numPacketsSent;
    }

    public int getNumPacketsRelayed() {
        return numPacketsRelayed;
    }

    public void setNumPacketsRelayed(int numPacketsRelayed) {
        this.numPacketsRelayed = numPacketsRelayed;
    }

    public long getSumPacketsSent() {
        return sumPacketsSent;
    }

    public void setSumPacketsSent(long sumPacketsSent) {
        this.sumPacketsSent = sumPacketsSent;
    }

    public int getNumPacketsReceived() {
        return numPacketsReceived;
    }

    public void setNumPacketsReceived(int numPacketsReceived) {
        this.numPacketsReceived = numPacketsReceived;
    }

    public long getSumPacketsReceived() {
        return sumPacketsReceived;
    }

    public void setSumPacketsReceived(long sumPacketsReceived) {
        this.sumPacketsReceived = sumPacketsReceived;
    }
}
