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

public class RegistryRequestsTaskInitiate extends Event {
    private static final Logger logger = LogManager.getLogger(RegistryRequestsTaskInitiate.class);

    private byte messageType;
    private int noOfPacketsToSend;

    public RegistryRequestsTaskInitiate() {

    }

    /**
     * byte: Message type; REGISTRY_REQUESTS_TASK_INITIATE
     * int: Number of data packets to send
     */
    public RegistryRequestsTaskInitiate(byte[] marshalledBytes) throws IOException {
        ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

        messageType = din.readByte();

        noOfPacketsToSend = din.readInt();

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
            dout.writeInt(noOfPacketsToSend);

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
        return Protocol.REGISTRY_REQUESTS_TASK_INITIATE;
    }

    public byte getMessageType() {
        return messageType;
    }

    public void setMessageType(byte messageType) {
        this.messageType = messageType;
    }

    public int getNoOfPacketsToSend() {
        return noOfPacketsToSend;
    }

    public void setNoOfPacketsToSend(int noOfPacketsToSend) {
        this.noOfPacketsToSend = noOfPacketsToSend;
    }
}
