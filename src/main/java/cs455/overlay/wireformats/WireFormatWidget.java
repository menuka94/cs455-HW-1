package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class WireFormatWidget {
    private int type;
    private long timestamp;
    private String identifier;
    private int tracker;

    public WireFormatWidget(byte[] marshalledBytes) throws IOException {
        ByteArrayInputStream baInputStream =
                new ByteArrayInputStream(marshalledBytes);
        DataInputStream din =
                new DataInputStream(new BufferedInputStream(baInputStream));
        type = din.readInt();
        timestamp = din.readLong();
        int identifierLength = din.readInt();
        byte[] identifierBytes = new byte[identifierLength];
        din.readFully(identifierBytes);
        identifier = new String(identifierBytes);
        tracker = din.readInt();
        baInputStream.close();
        din.close();
    }

    public int getType() {
        return type;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getIdentifier() {
        return identifier;
    }

    public int getTracker() {
        return tracker;
    }

    @Override
    public String toString() {
        return "WireFormatWidget{" +
                "type=" + type +
                ", timestamp=" + timestamp +
                ", identifier='" + identifier + '\'' +
                ", tracker=" + tracker +
                '}';
    }
}
