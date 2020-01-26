package cs455.overlay.node;

import cs455.overlay.wireformats.Event;

public class Registry implements Node {
    private static Registry instance;

    private Registry() {
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
