package cs455.overlay.wireformats;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import cs455.overlay.util.Validator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OverlayNodeReportsTaskFinished extends Event {
    private static final Logger logger = LogManager.getLogger(OverlayNodeReportsTaskFinished.class);
    private byte messageType;
    private byte ipAddressLength;
    private byte[] ipAddress;
    private int port;
    private int nodeId;

    public OverlayNodeReportsTaskFinished() {

    }

    /**
    }
     * byte: Message type; OVERLAY_NODE_REPORTS_TASK_FINISHED
     * byte: length of following "IP address" field
     * byte[^^]: Node IP address:
     * int: Node Port number:
     * int: nodeID
     */
    public OverlayNodeReportsTaskFinished(byte[] marshalledBytes) throws IOException {
        ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(baInputStream);

        messageType = din.readByte();

        Validator.validateEventType(messageType, Protocol.OVERLAY_NODE_REPORTS_TASK_FINISHED, logger);

        ipAddressLength = din.readByte();
        ipAddress = new byte[ipAddressLength];
        din.readFully(ipAddress, 0, ipAddressLength);
        nodeId = din.readInt();


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
            dout.writeByte(ipAddressLength);
            dout.write(ipAddress);
            dout.writeInt(nodeId);

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
        return Protocol.OVERLAY_NODE_REPORTS_TASK_FINISHED;
    }

    public byte getMessageType() {
        return messageType;
    }

    public void setMessageType(byte messageType) {
        this.messageType = messageType;
    }

    public byte getIpAddressLength() {
        return ipAddressLength;
    }

    public void setIpAddressLength(byte ipAddressLength) {
        this.ipAddressLength = ipAddressLength;
    }

    public byte[] getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(byte[] ipAddress) {
        this.ipAddress = ipAddress;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getNodeId() {
        return nodeId;
    }

    public void setNodeId(int nodeId) {
        this.nodeId = nodeId;
    }
}
