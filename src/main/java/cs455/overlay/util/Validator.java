package cs455.overlay.util;

import cs455.overlay.wireformats.ProtocolLookup;
import org.apache.logging.log4j.Logger;

public class Validator {
    public static void validateEventType(byte messageType, int expectedType, Logger logger) {
        if (messageType != expectedType) {
            logger.warn("Unexpected message type: " + ProtocolLookup.
                    getEventLiteral(Byte.toUnsignedInt(messageType)));
        }
    }
}
