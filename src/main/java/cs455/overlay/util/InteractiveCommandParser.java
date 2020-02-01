package cs455.overlay.util;

import java.util.Scanner;
import cs455.overlay.node.MessagingNode;
import cs455.overlay.node.Node;
import cs455.overlay.node.Registry;

public class InteractiveCommandParser extends Thread {
    private boolean acceptingCommands;
    private boolean isRegistry;

    public InteractiveCommandParser(Node node) {
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
            parseMessagingNodeCommands(scanner);
        }
    }

    private void parseRegistryCommands(Scanner scanner) {
        String nextCommand;
        while (acceptingCommands) {
            nextCommand = scanner.nextLine().trim();
            if (nextCommand.contains(Constants.LIST_MESSAGING_NODES)) {
                System.out.println("TODO: List messaging nodes");
            } else if (nextCommand.contains(Constants.LIST_ROUTING_TABLES)) {
                System.out.println("TODO: List routing tables");
            } else if (nextCommand.contains(Constants.START)) {
                int numberOfMessages = Integer.parseInt(nextCommand.split(" ")[1]);
                System.out.println("TODO: Start " + numberOfMessages);
            } else if (nextCommand.contains(Constants.SETUP_OVERLAY)) {
                int tableEntries = Integer.parseInt(nextCommand.split(" ")[1]);
                System.out.println("TODO: setup-overlay " + tableEntries);
            } else {
                System.out.println("Invalid command for the registry: " + nextCommand);
            }
        }

        scanner.close();
    }

    private void parseMessagingNodeCommands(Scanner scanner) {
        String nextCommand;
        while (acceptingCommands) {
            nextCommand = scanner.next();
        }

        scanner.close();
    }
}
