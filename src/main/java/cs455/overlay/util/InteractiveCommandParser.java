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

    public InteractiveCommandParser(Node node) {
        this.node = node;
        acceptingCommands = true;
        if (node instanceof Registry) {
            isRegistry = true;
        } else if (node instanceof MessagingNode) {
            isRegistry = false;
        }
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        if (isRegistry) {
            System.out.println("Enter commands for the registry: ");
            parseRegistryCommands(scanner);
        } else {
            System.out.println("Enter commands for the messaging node: ");
            try {
                parseMessagingNodeCommands(scanner);
            } catch (IOException e) {
                logger.error(e.getStackTrace());
            }
        }
    }

    private void parseRegistryCommands(Scanner scanner) {
        String nextCommand;
        Registry registry = (Registry) node;
        while (acceptingCommands) {
            nextCommand = scanner.nextLine().trim();
            if (nextCommand.contains(Constants.LIST_MESSAGING_NODES)) {
                registry.listMessagingNodes();
            } else if (nextCommand.contains(Constants.LIST_ROUTING_TABLES)) {
                System.out.println("TODO: List routing tables");
            } else if (nextCommand.contains(Constants.START)) {
                int numberOfMessages = Integer.parseInt(nextCommand.split(" ")[1]);
                System.out.println("TODO: Start " + numberOfMessages);
            } else if (nextCommand.contains(Constants.SETUP_OVERLAY)) {
                int tableEntries = Integer.parseInt(nextCommand.split(" ")[1]);
                System.out.println("TODO: setup-overlay " + tableEntries);
            } else if (nextCommand.trim().equals("")) {
                continue;
            } else {
                System.out.println("Invalid command for the registry: " + nextCommand);
            }
        }

        scanner.close();
    }

    private void parseMessagingNodeCommands(Scanner scanner) throws IOException {
        String nextCommand;
        MessagingNode messagingNode = (MessagingNode) node;
        while (acceptingCommands) {
            nextCommand = scanner.next().trim();
            if (nextCommand.contains(Constants.PRINT_COUNTERS_AND_DIAGNOSTICS)) {
                System.out.println("TODO: " + Constants.PRINT_COUNTERS_AND_DIAGNOSTICS);
            } else if (nextCommand.contains(Constants.EXIT_OVERLAY)) {
                messagingNode.exitOverlay();
            } else {
                System.out.println("Invalid command for messaging node: " + nextCommand);
            }
        }

        scanner.close();
    }
}
