package cs455.overlay.wireformats;

import cs455.overlay.util.Validator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;

public class RegistrySendsNodeManifest extends Event {
    private static final Logger logger = LogManager.getLogger(RegistrySendsNodeManifest.class);

    private int tableSize;
    private byte messageType;
    private int[] nodesIds;
    private byte[] ipAddressLengths;
    private byte[][] ipAddresses;
    private int[] ports;

    private int noOfNodeIds;
    private int[] allNodeIds;

    /**
     * byte: Message type; REGISTRY_SENDS_NODE_MANIFEST
     * byte: routing table size N R
     * =============================================================================
     * int: Node ID of node 1 hop away
     * byte: length of following "IP address" field
     * byte[^^]: IP address of node 1 hop away; from InetAddress.getAddress()
     * int: Port number of node 1 hop away
     * -----------------------------------------------------------------------------
     * int: Node ID of node 2 hops away
     * byte: length of following "IP address" field
     * byte[^^]: IP address of node 2 hops away; from InetAddress.getAddress()
     * int: Port number of node 2 hops away
     * -----------------------------------------------------------------------------
     * int: Node ID of node 4 hops away
     * byte: length of following "IP address" field
     * byte[^^]: IP address of node 4 hops away; from InetAddress.getAddress()
     * int: Port number of node 4 hops away
     * =============================================================================
     * byte: Number of node IDs in the system
     * int[^^]: List of all node IDs in the system [Note no IPs are included]
     */
    public RegistrySendsNodeManifest(byte[] marshalledBytes) throws IOException {
        ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

        messageType = din.readByte();

        Validator.validateEventType(messageType, Protocol.REGISTRY_SENDS_NODE_MANIFEST, logger);

        tableSize = din.readByte();
        nodesIds = new int[tableSize];
        ports = new int[tableSize];
        ipAddressLengths = new byte[tableSize];
        ipAddresses = new byte[tableSize][];

        for (int i = 0; i < tableSize; i++) {
            nodesIds[i] = din.readInt();
            ipAddressLengths[i] = din.readByte();
            ipAddresses[i] = new byte[ipAddressLengths[i]];
            din.readFully(ipAddresses[i], 0, ipAddressLengths[i]);
            ports[i] = din.readInt();
        }

        noOfNodeIds = din.readByte();
        allNodeIds = new int[noOfNodeIds];
        for (int i = 0; i < noOfNodeIds; i++) {
            allNodeIds[i] = din.readInt();
        }
    }

    @Override
    public byte[] getBytes() {
        byte[] marshalledBytes = null;
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

        try {
            dout.writeByte(getType());
            dout.writeInt(tableSize);

            for (int i = 0; i < tableSize; i++) {
                dout.writeInt(nodesIds[i]);
                dout.writeByte(ipAddressLengths[i]);
                dout.write(ipAddresses[i]);
                dout.writeInt(ports[i]);
            }

            dout.writeByte(noOfNodeIds);
            for (int i = 0; i < noOfNodeIds; i++) {
                dout.writeInt(allNodeIds[i]);
            }

            dout.flush();

            marshalledBytes = baOutputStream.toByteArray();
        } catch (IOException e) {
            logger.error(e.getStackTrace());
        } finally {
            try {
                baOutputStream.close();
                dout.close();
            } catch (IOException e) {
                logger.error(e.getStackTrace());
            }
        }

        return marshalledBytes;
    }

    public int getTableSize() {
        return tableSize;
    }


    public void setTableSize(int tableSize) {
        this.tableSize = tableSize;
    }

    @Override
    public int getType() {
        return Protocol.REGISTRY_SENDS_NODE_MANIFEST;
    }

    public int[] getNodesIds() {
        return nodesIds;
    }

    public void setNodesIds(int[] nodesIds) {
        this.nodesIds = nodesIds;
    }

    public byte[] getIpAddressLengths() {
        return ipAddressLengths;
    }

    public void setIpAddressLengths(byte[] ipAddressLengths) {
        this.ipAddressLengths = ipAddressLengths;
    }

    public byte[][] getIpAddresses() {
        return ipAddresses;
    }

    public void setIpAddresses(byte[][] ipAddresses) {
        this.ipAddresses = ipAddresses;
    }

    public int[] getPorts() {
        return ports;
    }

    public void setPorts(int[] ports) {
        this.ports = ports;
    }

    public int getNoOfNodeIds() {
        return noOfNodeIds;
    }

    public void setNoOfNodeIds(int noOfNodeIds) {
        this.noOfNodeIds = noOfNodeIds;
    }

    public int[] getAllNodeIds() {
        return allNodeIds;
    }

    public void setAllNodeIds(int[] allNodeIds) {
        this.allNodeIds = allNodeIds;
    }
}
