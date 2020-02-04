package cs455.overlay.wireformats;

public class RegistryRequestsTrafficSummary extends Event {

    /**
     * byte: Message Type; REGISTRY_REQUESTS_TRAFFIC_SUMMARY
     */
    public RegistryRequestsTrafficSummary(byte[] marshalledBytes) {

    }

    @Override
    public byte[] getBytes() {
        return new byte[0];
    }

    @Override
    public int getType() {
        return Protocol.REGISTRY_REQUESTS_TRAFFIC_SUMMARY;
    }
}
