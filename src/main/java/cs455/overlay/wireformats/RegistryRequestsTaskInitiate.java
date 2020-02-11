package cs455.overlay.wireformats;

public class RegistryRequestsTaskInitiate extends Event {

    private byte messageType;
    private int noOfPackets;

    /**
     * byte: Message type; REGISTRY_REQUESTS_TASK_INITIATE
     * int: Number of data packets to send
     */
    public RegistryRequestsTaskInitiate(byte[] marshalledBytes) {

    }
    @Override
    public byte[] getBytes() {
        return new byte[0];
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

    public int getNoOfPackets() {
        return noOfPackets;
    }

    public void setNoOfPackets(int noOfPackets) {
        this.noOfPackets = noOfPackets;
    }
}
