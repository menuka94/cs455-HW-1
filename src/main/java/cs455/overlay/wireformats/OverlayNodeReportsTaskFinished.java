package cs455.overlay.wireformats;

public class OverlayNodeReportsTaskFinished implements Event {
    @Override
    public byte[] getBytes() {
        return new byte[0];
    }

    @Override
    public int getType() {
        return Protocol.OVERLAY_NODE_REPORTS_TASK_FINISHED;
    }
}
