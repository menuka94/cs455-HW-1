package cs455.overlay.wireformats;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class OverlayNodeSendsDeregistration extends Event {
    private static final Logger logger = LogManager.getLogger(OverlayNodeSendsDeregistration.class);

    private byte messageType;
    private byte ipAddressLength;
    private byte[] ipAddress;
    private int port;
    private int nodeId;
    private Socket socket;

    public OverlayNodeSendsDeregistration() {

    }

    /**
     * byte: Message Type (OVERLAY_NODE_SENDS_DEREGISTRATION)
     * byte: length of following "IP address" field
     * byte[^^]: IP address; from InetAddress.getAddress()
     * int: Port number
     * int: assigned Node ID
     */
    public OverlayNodeSendsDeregistration(byte[] marshalledBytes) throws IOException {
        ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

        messageType = din.readByte();

        if (messageType != Protocol.OVERLAY_NODE_SENDS_DEREGISTRATION) {
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

    public void setNodeId(int nodeId) {
        this.nodeId = nodeId;
    }

    public byte getIpAddressLength() {
        return ipAddressLength;
    }

    public byte[] getIpAddress() {
        return ipAddress;
    }

    public int getPort() {
        return port;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setIpAddressLength(byte ipAddressLength) {
        this.ipAddressLength = ipAddressLength;
    }

    public void setIpAddress(byte[] ipAddress) {
        this.ipAddress = ipAddress;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public byte getMessageType() {
        return messageType;
    }

    public void setMessageType(byte messageType) {
        this.messageType = messageType;
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
            dout.writeByte(ipAddressLength);
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
        return Protocol.OVERLAY_NODE_SENDS_DEREGISTRATION;
    }
}
