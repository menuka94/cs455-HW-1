package cs455.overlay.node;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import cs455.overlay.transport.TCPConnectionsCache;
import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.util.InteractiveCommandParser;
import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.OverlayNodeSendsRegistration;
import cs455.overlay.wireformats.Protocol;
import cs455.overlay.wireformats.RegistryReportsRegistrationStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Registry implements Node {
    private static final Logger logger = LogManager.getLogger(Registry.class);
    private int port;
    private InteractiveCommandParser commandParser;
    private TCPServerThread tcpServerThread;
    private LinkedBlockingQueue<Event> eventQueue;

    private class RegistryEventHandlerThread extends Thread {
        private Logger logger = LogManager.getLogger(RegistryEventHandlerThread.class);

        @Override
        public void run() {
            while (true) {
                try {
                    Event event = eventQueue.take();

                    int type = event.getType();

                    if (type == Protocol.OVERLAY_NODE_SENDS_REGISTRATION) {
                        registerOverlayNode(event);
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
                        logger.warn("Unknown event type: " + type);
                    }
                } catch (InterruptedException e) {
                    logger.error(e.getStackTrace());
                }
            }
        }
    }

    private Registry(int port) throws IOException {
        tcpServerThread = new TCPServerThread(port);
        tcpServerThread.start();
        commandParser = new InteractiveCommandParser(this);
        commandParser.start();
        eventQueue = new LinkedBlockingQueue<>();
        RegistryEventHandlerThread eventHandlerThread = new RegistryEventHandlerThread();
        eventHandlerThread.start();
    }

    public static void main(String[] args) throws IOException {
        logger.info("main()");
        int port = Integer.parseInt(args[0]);
        Registry registry = new Registry(port);
    }

    @Override
    public void onEvent(Event event) {
        boolean offer = eventQueue.offer(event);
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

    private void registerOverlayNode(Event event) {
        logger.info("registerOverlayNode()");
        OverlayNodeSendsRegistration registrationEvent = (OverlayNodeSendsRegistration) event;
        RegistryReportsRegistrationStatus responseEvent =
                new RegistryReportsRegistrationStatus();

        // Checking if there is a mismatch in the address that is specified in the registration
        // request and the IP address of the request (the socket’s input stream).
        byte[] registrationEventIpAddress = registrationEvent.getIpAddress();
        Socket socket = registrationEvent.getSocket();

    }
}
