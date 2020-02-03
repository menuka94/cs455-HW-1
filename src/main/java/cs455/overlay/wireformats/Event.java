package cs455.overlay.wireformats;

import java.net.Socket;
import cs455.overlay.node.MessagingNode;

public abstract class Event {
    private MessagingNode messagingNode;

    public MessagingNode getMessagingNode() {
        return messagingNode;
    }

    public void setMessagingNode(MessagingNode messagingNode) {
        this.messagingNode = messagingNode;
    }

    public abstract byte[] getBytes();

    public abstract int getType();
}
