package cs455.overlay.transport;

import java.net.Socket;
import java.util.HashMap;
import java.util.Set;

public class TCPConnectionsCache {
    private static HashMap<Socket, TCPConnection> cachedConnections
            = new HashMap<>();

    public static void addConnection(Socket socket, TCPConnection tcpConnection) {
        cachedConnections.put(socket, tcpConnection);
    }

    public static TCPConnection getConnection(Socket socket) {
        return cachedConnections.get(socket);
    }

    public static void removeConnection(Socket socket) {
        cachedConnections.remove(socket);
    }

    public static synchronized boolean containsConnection(Socket socket) {
        return cachedConnections.containsKey(socket);
    }

    public static void printConnections() {
        Set<Socket> sockets = cachedConnections.keySet();
        for (Socket socket : sockets) {
            System.out.println(cachedConnections.get(socket));
        }
    }
}
