package cs455.overlay.wireformats;

public class RegistryReportsDeregistrationStatus implements Event {

    @Override
    public byte[] getBytes() {
        return new byte[0];
    }

    @Override
    public int getType() {
        return Protocol.REGISTRY_REPORTS_DEREGISTRATION_STATUS;
    }
}
