package cs455.overlay.util;

import java.util.ArrayList;
import cs455.overlay.wireformats.OverlayNodeReportsTrafficSummary;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StatisticsCollectorAndDisplay {
    private static final Logger logger = LogManager.getLogger(StatisticsCollectorAndDisplay.class);
    private int totalNodes;
    private ArrayList<Row> rows;
    private int grandTotalSent;
    private int grandTotalReceived;
    private int grantTotalRelayed;
    private long grandTotalValuesSent;
    private long grandTotalValuesReceived;

    private class Row {
        int nodeId;
        int packetsSent;
        int packetsReceived;
        int packetsRelayed;
        long sumValuesSent;
        long sumValuesReceived;
    }

    public StatisticsCollectorAndDisplay(int totalNodes) {
        this.totalNodes = totalNodes;
        rows = new ArrayList<>();
    }

    public void add(OverlayNodeReportsTrafficSummary trafficSummaryEvent) {
        logger.debug("Adding row - Node " + trafficSummaryEvent.getNodeId());
        Row row = new Row();
        row.nodeId = trafficSummaryEvent.getNodeId();
        row.packetsReceived = trafficSummaryEvent.getNumPacketsReceived();
        row.packetsSent = trafficSummaryEvent.getNumPacketsSent();
        row.packetsRelayed = trafficSummaryEvent.getNumPacketsRelayed();
        row.sumValuesReceived = trafficSummaryEvent.getSumPacketsReceived();
        row.sumValuesSent = trafficSummaryEvent.getSumPacketsSent();

        rows.add(row);

        grandTotalReceived += row.packetsReceived;
        grandTotalSent += row.packetsSent;
        grantTotalRelayed += row.packetsRelayed;
        grandTotalValuesReceived += row.sumValuesReceived;
        grandTotalValuesSent += row.sumValuesSent;

        if (rows.size() == totalNodes) {
            printResults();
        }
    }

    private void printResults() {
        System.out.println("Node | Packets | Packets  | Packets | Sum Values    | Sum Values");
        System.out.println("ID   | Sent    | Received | Relayed | Sent          | Received");
        System.out.println("_________________________________________________________________");
        for (Row row : rows) {
            System.out.format(" %4s|%8s|%10s|%9s|%15s|%15s%n", row.nodeId, row.packetsSent,
                    row.packetsReceived, row.packetsRelayed, row.sumValuesSent,
                    row.sumValuesReceived);
        }
        System.out.format("Sum  |%8s|%10s|%9s|%15s|%15s%n", grandTotalSent, grandTotalReceived,
                grantTotalRelayed, grandTotalValuesSent, grandTotalValuesReceived);
    }
}
