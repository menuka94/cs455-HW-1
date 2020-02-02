package cs455.overlay.node;

import java.io.IOException;
import cs455.overlay.transport.TCPConnectionsCache;
import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.util.InteractiveCommandParser;
import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.Protocol;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Registry implements Node {
    private static final Logger logger = LogManager.getLogger(Registry.class);
    private int port;
    private InteractiveCommandParser commandParser;
    private TCPServerThread tcpServerThread;

    private Registry(int port) throws IOException {
        tcpServerThread = new TCPServerThread(port);
        tcpServerThread.start();
        commandParser = new InteractiveCommandParser(this);
        commandParser.start();
    }

    public static void main(String[] args) throws IOException {
        logger.info("main()");
        int port = Integer.parseInt(args[0]);
        Registry registry = new Registry(port);
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

    public void listMessagingNodes() {
        TCPConnectionsCache.printConnections();
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
