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

public class RegistryReportsRegistrationStatus extends Event {
    private static final Logger logger = LogManager.getLogger(RegistryReportsRegistrationStatus.class);
    private byte messageType;
    private int successStatus;
    private byte lengthOfInfoString;
    private byte[] infoString;

    public RegistryReportsRegistrationStatus() {
    }

    /**
     * byte: Message type (REGISTRY_REPORTS_REGISTRATION_STATUS)
     * int: Success status; Assigned ID if successful, -1 in case of a failure
     * byte: Length of following "Information string" field
     * byte[^^]: Information string; ASCII charset
     */
    public RegistryReportsRegistrationStatus(byte[] marshalledBytes) throws IOException {
        logger.info("constructor()");
        ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

        messageType = din.readByte();
        if (messageType != Protocol.REGISTRY_REPORTS_REGISTRATION_STATUS) {
            logger.warn("Incorrect message type: " + ProtocolLookup.getEventLiteral(messageType));
        }

        successStatus = din.readInt();
        lengthOfInfoString = din.readByte();
        infoString = new byte[lengthOfInfoString];
        din.readFully(infoString, 0, lengthOfInfoString);

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
            dout.writeInt(successStatus);
            dout.writeByte(lengthOfInfoString);
            dout.write(infoString);

            dout.flush();

            marshalledBytes = baOutputStream.toByteArray();
            baOutputStream.close();
            dout.close();
        } catch (IOException e) {
            logger.error(e.getStackTrace());
        }

        return marshalledBytes;
    }

    @Override
    public int getType() {
        return Protocol.REGISTRY_REPORTS_REGISTRATION_STATUS;
    }

    public int getSuccessStatus() {
        return successStatus;
    }

    public void setSuccessStatus(int successStatus) {
        this.successStatus = successStatus;
    }

    public byte getLengthOfInfoString() {
        return lengthOfInfoString;
    }

    public void setLengthOfInfoString(byte lengthOfInfoString) {
        this.lengthOfInfoString = lengthOfInfoString;
    }

    public byte[] getInfoString() {
        return infoString;
    }

    public void setInfoString(byte[] infoString) {
        this.infoString = infoString;
    }
}
