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

public class OverlayNodeSendsRegistration implements Event {
    private static final Logger logger = LogManager.getLogger(OverlayNodeSendsRegistration.class);
    private byte[] ipAddress;

    private byte nodeIPAddressLength;
    private byte[] nodeIPAddress;
    private int nodePort;

    public OverlayNodeSendsRegistration() {

    }

    public OverlayNodeSendsRegistration(byte[] marshalledBytes) throws IOException {
        ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

        byte messageType = din.readByte();

        if (messageType != Protocol.OVERLAY_NODE_SENDS_REGISTRATION) {
            logger.warn("Unexpected message type: " + ProtocolLookup.getEventLiteral(Byte.toUnsignedInt(messageType)));
        }

        nodeIPAddressLength = din.readByte();
        nodeIPAddress = new byte[nodeIPAddressLength];
        din.readFully(nodeIPAddress, 0, nodeIPAddressLength);

        nodePort = din.readInt();
        baInputStream.close();
        din.close();
    }

    public void setNodeIPAddressLength(byte nodeIPAddressLength) {
        this.nodeIPAddressLength = nodeIPAddressLength;
    }

    public void setNodeIPAddress(byte[] nodeIPAddress) {
        this.nodeIPAddress = nodeIPAddress;
    }

    public void setNodePort(int nodePort) {
        this.nodePort = nodePort;
    }

    public void setIpAddress(byte[] ipAddress) {
        this.ipAddress = ipAddress;
    }

    public byte[] getIpAddress() {
        return ipAddress;
    }

    @Override
    public byte[] getBytes() {
        byte[] marshalledBytes = null;
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

        try {
            dout.writeByte(getType());
            dout.writeByte(nodeIPAddressLength);
            dout.write(nodeIPAddress);
            dout.writeInt(nodePort);

            dout.flush();

            marshalledBytes = baOutputStream.toByteArray();

            baOutputStream.close();
            dout.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return marshalledBytes;
    }


    @Override
    public int getType() {
        return Protocol.OVERLAY_NODE_SENDS_REGISTRATION;
    }
}
