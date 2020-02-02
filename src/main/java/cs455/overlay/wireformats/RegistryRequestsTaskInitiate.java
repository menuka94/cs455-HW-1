package cs455.overlay.wireformats;

public class RegistryRequestsTaskInitiate extends Event {

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
}
