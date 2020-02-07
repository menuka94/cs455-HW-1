package cs455.overlay.wireformats;

import java.net.Socket;
import cs455.overlay.node.MessagingNode;

public abstract class Event {
    private MessagingNode messagingNode;
    private Socket socket;

    public MessagingNode getMessagingNode() {
        return messagingNode;
    }

    public void setMessagingNode(MessagingNode messagingNode) {
        this.messagingNode = messagingNode;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public abstract byte[] getBytes();

    public abstract int getType();
}
