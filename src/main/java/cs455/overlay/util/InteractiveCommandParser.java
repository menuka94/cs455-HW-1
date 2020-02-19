package cs455.overlay.util;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Scanner;

import cs455.overlay.node.MessagingNode;
import cs455.overlay.node.Node;
import cs455.overlay.node.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class InteractiveCommandParser extends Thread {
    private static final Logger logger = LogManager.getLogger(InteractiveCommandParser.class);
    private boolean acceptingCommands;
    private boolean isRegistry;
    private Node node;
    private Scanner scanner;

    public InteractiveCommandParser(Node node) {
        this.node = node;
        scanner = new Scanner(System.in);
        acceptingCommands = true;
        if (node instanceof Registry) {
            isRegistry = true;
        } else if (node instanceof MessagingNode) {
            isRegistry = false;
        }
    }

    @Override
    public void run() {
        if (isRegistry) {
            System.out.println("Enter commands for the registry: ");
            parseRegistryCommands();
        } else {
            System.out.println("Enter commands for the messaging node: ");
            try {
                parseMessagingNodeCommands();
            } catch (IOException e) {
                logger.error(e.getStackTrace());
            }
        }
    }

    private void parseRegistryCommands() {
        String nextCommand;
        Registry registry = (Registry) node;
        while (acceptingCommands) {
            nextCommand = scanner.nextLine().trim();
            if (nextCommand.contains(Constants.LIST_MESSAGING_NODES)) {
                registry.listMessagingNodes();
            } else if (nextCommand.contains(Constants.LIST_ROUTING_TABLES)) {
                registry.listRoutingTables();
            } else if (nextCommand.contains(Constants.START)) {
                try {
                    int numberOfMessages = Integer.parseInt(nextCommand.split(" ")[1]);
                    ((Registry) node).start(numberOfMessages);
                } catch (NumberFormatException e) {
                    logger.error("Invalid number of messages entered");
                }
            } else if (nextCommand.contains(Constants.SETUP_OVERLAY)) {
                String[] args = nextCommand.split("\\s+");
                int tableSize = 0;
                if (args.length == 2) {
                    try {
                        tableSize = Integer.parseInt(args[1]);
                    } catch (NumberFormatException e) {
                        System.out.println("Enter a whole number as the table size");
                        continue;
                    }
                } else {
                    System.out.println("Table size not provided. Proceeding with the default " +
                            "value: 3");
                    tableSize = 3;
                }
                registry.setupOverlay(tableSize);
            } else if (nextCommand.trim().equals("")) {
                continue;
            } else {
                System.out.println("Invalid command for the registry: " + nextCommand);
            }
        }

        logger.info("Shutting down the registry ...");
        scanner.close();
    }

    private void parseMessagingNodeCommands() throws IOException {
        String nextCommand;
        MessagingNode messagingNode = (MessagingNode) node;
        while (acceptingCommands) {
            nextCommand = scanner.next().trim();
            if (nextCommand.contains(Constants.PRINT_COUNTERS_AND_DIAGNOSTICS)) {
                System.out.println("TODO: " + Constants.PRINT_COUNTERS_AND_DIAGNOSTICS);
            } else if (nextCommand.contains(Constants.EXIT_OVERLAY)) {
                messagingNode.exitOverlay();
            } else if (nextCommand.contains("print-routing-table")) {
                messagingNode.printRoutingTable();
            } else if (nextCommand.contains("print-id")) {
                messagingNode.printNodeId();
            } else {
                System.out.println("Invalid command for messaging node: " + nextCommand);
            }
        }
        logger.info("Shutting down node ...");
        scanner.close();
    }

    public void stopAcceptingCommands() {
        acceptingCommands = false;
        if (isRegistry) {
            parseRegistryCommands();
        } else {
            try {
                parseMessagingNodeCommands();
            } catch (IOException e) {
                logger.error("Error stopping interactive command parser");
                logger.error(e.getStackTrace());
            }
        }
    }
}
