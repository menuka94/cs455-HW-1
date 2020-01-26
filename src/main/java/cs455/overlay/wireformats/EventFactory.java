package cs455.overlay.wireformats;

public class EventFactory {
    private static EventFactory instance;

    private EventFactory() {

    }

    public static EventFactory getInstance() {
        instance = new EventFactory();
        return instance;
    }
}
