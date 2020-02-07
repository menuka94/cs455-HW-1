package cs455.overlay.wireformats;

import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EventFactory {
    private static final Logger logger = LogManager.getLogger(EventFactory.class);
    private static EventFactory instance;

    private EventFactory() {

    }

    public synchronized static EventFactory getInstance() {
        if (instance == null) {
            instance = new EventFactory();
        }
        return instance;
    }

    public Event getEvent(byte[] data, Socket socket)
            throws IOException {
        byte b = ByteBuffer.wrap(data).get(0);
        logger.info("Event type: " + (int) b);
        switch ((int) b) {
            case Protocol.OVERLAY_NODE_SENDS_DATA:
                return new OverlayNodeSendsData(data);
            case Protocol.OVERLAY_NODE_SENDS_REGISTRATION:
                logger.info("OVERLAY_NODE_SENDS_REGISTRATION");
                OverlayNodeSendsRegistration registrationEvent = new OverlayNodeSendsRegistration(data);
                registrationEvent.setSocket(socket);
                return registrationEvent;
            case Protocol.REGISTRY_REPORTS_REGISTRATION_STATUS:
                logger.info("REGISTRY_REPORTS_REGISTRATION_STATUS");
                RegistryReportsRegistrationStatus registrationResponseEvent = new RegistryReportsRegistrationStatus(data);
                return registrationResponseEvent;
            case Protocol.OVERLAY_NODE_SENDS_DEREGISTRATION:
                logger.info("OVERLAY_NODE_SENDS_DEREGISTRATION");
                OverlayNodeSendsDeregistration deregistrationEvent = new OverlayNodeSendsDeregistration(data);
                deregistrationEvent.setSocket(socket);
                return deregistrationEvent;
            case Protocol.REGISTRY_REPORTS_DEREGISTRATION_STATUS:
                logger.info("REGISTRY_REPORTS_DEREGISTRATION_STATUS");
                return new RegistryReportsDeregistrationStatus(data);
            case Protocol.REGISTRY_SENDS_NODE_MANIFEST:
                logger.info("REGISTRY_SENDS_NODE_MANIFEST");
                RegistrySendsNodeManifest registrySendsNodeManifest = new RegistrySendsNodeManifest(data);
                registrySendsNodeManifest.setSocket(socket);
                return registrySendsNodeManifest;
            case Protocol.NODE_REPORTS_OVERLAY_SETUP_STATUS:
                logger.info("NODE_REPORTS_OVERLAY_SETUP_STATUS");
                return new NodeReportsOverlaySetupStatus(data);
            case Protocol.REGISTRY_REQUESTS_TASK_INITIATE:
                logger.info("REGISTRY_REQUESTS_TASK_INITIATE");
                return new RegistryRequestsTaskInitiate(data);
            case Protocol.OVERLAY_NODE_REPORTS_TASK_FINISHED:
                logger.info("OVERLAY_NODE_REPORTS_TASK_FINISHED");
                return new OverlayNodeReportsTaskFinished(data);
            case Protocol.REGISTRY_REQUESTS_TRAFFIC_SUMMARY:
                logger.info("REGISTRY_REQUESTS_TRAFFIC_SUMMARY");
                return new RegistryRequestsTrafficSummary(data);
            case Protocol.OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY:
                logger.info("OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY");
                return new OverlayNodeReportsTrafficSummary(data);
            default:
                logger.error("Unknown event type");
                return null;
        }

    }
}
