package cs455.overlay.wireformats;

public class RegistryReportsRegistrationStatus implements Event {

    @Override
    public byte[] getBytes() {
        return new byte[0];
    }

    @Override
    public int getType() {
        return Protocol.REGISTRY_REPORTS_REGISTRATION_STATUS;
    }
}
