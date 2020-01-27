package cs455.overlay.node;

import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.Protocol;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Registry implements Node {
    private static final Logger logger = LogManager.getLogger(Registry.class);
    private static int port;

    private static Registry instance;

    private Registry() {
    }

    public static void main(String[] args) {
        logger.info("Port Number received: " + args[0]);
    }

    public synchronized static Registry getInstance(int registryPort) {
        if (instance == null) {
            port = registryPort;
            instance = new Registry();
        }
        return instance;
    }

    @Override
    public void onEvent(Event event) {
        int type = event.getType();

        if (type == Protocol.OVERLAY_NODE_SENDS_REGISTRATION) {
            registerOverlayNode();
        } else if (type == Protocol.OVERLAY_NODE_SENDS_DEREGISTRATION) {
            deregisterOverlayNode();
        } else if (type == Protocol.OVERLAY_NODE_SENDS_DATA) {
            respondToOverlayNodeSendsData();
        } else if (type == Protocol.NODE_REPORTS_OVERLAY_SETUP_STATUS) {
            respondToNodeReportsOverlaySetupStatus();
        } else if (type == Protocol.OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY) {
            respondToOverlayNodeReportsTrafficSummary();
        } else if (type == Protocol.OVERLAY_NODE_REPORTS_TASK_FINISHED) {
            respondToOverlayNodeReportsTaskFinished();
        } else {
            logger.warn("Unknown event type");
        }
    }

    private void respondToOverlayNodeReportsTaskFinished() {

    }

    private void respondToOverlayNodeReportsTrafficSummary() {

    }

    private void respondToNodeReportsOverlaySetupStatus() {

    }

    private void respondToOverlayNodeSendsData() {

    }

    private void deregisterOverlayNode() {

    }

    private void registerOverlayNode() {

    }
}
