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
    private int messageType;
    private int successStatus;
    private byte lengthOfInfoString;
    private String infoString;

    public static void main(String[] args) throws IOException {
        RegistryReportsRegistrationStatus event = new RegistryReportsRegistrationStatus();
        String infoString = "test infoString";
        event.setInfoString(infoString);
        event.setLengthOfInfoString((byte) infoString.getBytes().length);
        event.setMessageType((byte) event.getType());
        event.setSuccessStatus(124);

        byte[] marshalledBytes = event.getBytes();
        RegistryReportsRegistrationStatus unmarshalledEvent = new RegistryReportsRegistrationStatus(marshalledBytes);
        System.out.println("infoString: " + unmarshalledEvent.infoString);
        System.out.println("messageType: " + unmarshalledEvent.getType());
        System.out.println("Success Status: " + unmarshalledEvent.getSuccessStatus());

    }

    public RegistryReportsRegistrationStatus() {
    }


    /**
     * byte: Message type (REGISTRY_REPORTS_REGISTRATION_STATUS)
     * int: Success status; Assigned ID if successful, -1 in case of a failure
     * byte: Length of following "Information string" field
     * byte[^^]: Information string; ASCII charset
     */
    public RegistryReportsRegistrationStatus(byte[] marshalledBytes) throws IOException {
        ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

        messageType = din.readByte();
        if (messageType != Protocol.REGISTRY_REPORTS_REGISTRATION_STATUS) {
            logger.warn("Incorrect message type: " + ProtocolLookup.getEventLiteral(messageType));
        }

        successStatus = din.readInt();
        lengthOfInfoString = din.readByte();
        byte[] byteInfoString = new byte[lengthOfInfoString];
        din.readFully(byteInfoString, 0, lengthOfInfoString);

        infoString = new String(byteInfoString);

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

    public String getInfoString() {
        return infoString;
    }

    public void setInfoString(String infoString) {
        this.infoString = infoString;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }
}
