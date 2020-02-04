package cs455.overlay.wireformats;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;

public class RegistryReportsDeregistrationStatus extends Event {
    private final Logger logger = LogManager.getLogger(RegistryReportsDeregistrationStatus.class);

    private int messageType;
    private byte ipAddressLength;
    private byte[] ipAddress;
    int port;
    int nodeId;

    public static void main(String[] args) {

    }

    /**
     * byte: Message Type (OVERLAY_NODE_SENDS_DEREGISTRATION)
     * byte: length of following "IP address" field
     * byte[^^]: IP address; from InetAddress.getAddress()
     * int: Port number
     * int: assigned Node ID
     */
    public RegistryReportsDeregistrationStatus(byte[] marshalledBytes) throws IOException {
        ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

        byte messageType = din.readByte();

        if (messageType != Protocol.REGISTRY_REPORTS_DEREGISTRATION_STATUS) {
            logger.warn("Unexpected message type: " + ProtocolLookup.
                    getEventLiteral(Byte.toUnsignedInt(messageType)));
        }

        ipAddressLength = din.readByte();
        ipAddress = new byte[ipAddressLength];
        din.readFully(ipAddress, 0, ipAddressLength);

        port = din.readInt();
        nodeId = din.readInt();
        baInputStream.close();
        din.close();
    }

    public int getMessageType() {
        return messageType;
    }

    public byte[] getIpAddress() {
        return ipAddress;
    }

    public int getPort() {
        return port;
    }

    public int getNodeId() {
        return nodeId;
    }

    @Override
    public byte[] getBytes() {
        byte[] marshalledBytes = null;
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

        try {
            dout.writeByte(getType());
            dout.write(ipAddressLength);
            dout.write(ipAddress);
            dout.writeInt(port);
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
        return Protocol.REGISTRY_REPORTS_DEREGISTRATION_STATUS;
    }


}
