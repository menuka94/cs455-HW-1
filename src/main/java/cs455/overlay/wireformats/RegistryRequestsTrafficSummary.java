package cs455.overlay.wireformats;

public class RegistryRequestsTrafficSummary implements Event {
    @Override
    public byte[] getBytes() {
        return new byte[0];
    }

    @Override
    public int getType() {
        return Protocol.REGISTRY_REQUESTS_TRAFFIC_SUMMARY;
    }
}
