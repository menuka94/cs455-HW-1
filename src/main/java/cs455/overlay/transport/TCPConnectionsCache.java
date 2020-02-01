package cs455.overlay.transport;

import java.util.HashMap;

public class TCPConnectionsCache {
    private static HashMap<String, TCPConnection> cachedConnections
            = new HashMap<>();

    public static void addConnection(String id, TCPConnection tcpConnection) {
        cachedConnections.put(id, tcpConnection);
    }

    public static TCPConnection getConnection(String id) {
        return cachedConnections.get(id);
    }

    public static void removeConnection(String id) {
        cachedConnections.remove(id);
    }
}
