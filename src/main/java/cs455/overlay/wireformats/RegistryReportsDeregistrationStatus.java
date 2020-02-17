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

public class RegistryReportsDeregistrationStatus extends Event {
    private final Logger logger = LogManager.getLogger(RegistryReportsDeregistrationStatus.class);

    private int messageType;
    private int successStatus;
    private byte lengthOfInfoString;
    private String infoString;
    private Socket socket;

    public static void main(String[] args) {

    }

    public RegistryReportsDeregistrationStatus() {
    }

    /**
     * byte: Message type (REGISTRY_REPORTS_DEREGISTRATION_STATUS)
     * int: Success status; Assigned ID if successful, -1 in case of a failure
     * byte: Length of following "Information string" field
     * byte[^^]: Information string; ASCII charset
     */
    public RegistryReportsDeregistrationStatus(byte[] marshalledBytes) throws IOException {
        ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

        byte messageType = din.readByte();

        if (messageType != Protocol.REGISTRY_REPORTS_DEREGISTRATION_STATUS) {
            logger.warn("Unexpected message type: " + ProtocolLookup.
                    getEventLiteral(Byte.toUnsignedInt(messageType)));
        }

        successStatus = din.readInt();
        lengthOfInfoString = din.readByte();
        byte[] byteInfoString = new byte[lengthOfInfoString];
        din.readFully(byteInfoString, 0, lengthOfInfoString);

        infoString = new String(byteInfoString);

        baInputStream.close();
        din.close();
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setSuccessStatus(int successStatus) {
        this.successStatus = successStatus;
    }

    public void setLengthOfInfoString(byte lengthOfInfoString) {
        this.lengthOfInfoString = lengthOfInfoString;
    }

    public void setInfoString(String infoString) {
        this.infoString = infoString;
    }

    public int getSuccessStatus() {
        return successStatus;
    }

    public String getInfoString() {
        return infoString;
    }

    @Override
    public byte[] getBytes() {
        byte[] marshalledBytes = null;
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

        try {
            dout.writeByte(getType());
            dout.writeInt(successStatus);
            dout.writeByte(lengthOfInfoString);
            dout.write(infoString.getBytes());

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
