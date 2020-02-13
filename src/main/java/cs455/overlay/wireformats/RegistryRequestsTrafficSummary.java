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

public class RegistryRequestsTrafficSummary extends Event {
    private static final Logger logger = LogManager.getLogger(RegistryRequestsTrafficSummary.class);
    private byte messageType;

    public RegistryRequestsTrafficSummary() {

    }

    /**
     * byte: Message Type; REGISTRY_REQUESTS_TRAFFIC_SUMMARY
     */
    public RegistryRequestsTrafficSummary(byte[] marshalledBytes) throws IOException {
        ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(baInputStream);

        messageType = din.readByte();

        Validator.validateEventType(messageType, Protocol.REGISTRY_REQUESTS_TRAFFIC_SUMMARY, logger);

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
        return Protocol.REGISTRY_REQUESTS_TRAFFIC_SUMMARY;
    }
}
