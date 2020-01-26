package cs455.overlay.node;

import cs455.overlay.wireformats.Event;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Registry implements Node {
    private static final Logger logger = LogManager.getLogger(Registry.class);

    private static Registry instance;

    private Registry() {
    }

    public static void main(String[] args) {
        logger.info("Port Number received: " + args[0]);
    }

    public static Registry getInstance() {
        if (instance == null) {
            instance = new Registry();
        }
        return instance;
    }

    @Override
    public void onEvent(Event event) {

    }
}
