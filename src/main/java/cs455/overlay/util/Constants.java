package cs455.overlay.util;

public class Constants {
    public static final String LOG_PREFIX_INFO = "[INFO] ";
    public static final String LOG_PREFIX_WARN = "[WARN] ";
    public static final String LOG_PREFIX_ERROR = "[ERROR] ";


    // Registry Commands
    public static final String LIST_MESSAGING_NODES = "list-messaging-nodes";
    public static final String LIST_ROUTING_TABLES = "list-routing-tables";
    public static final String SETUP_OVERLAY = "setup-overlay";
    public static final String START = "start";

    // MessagingNode Commands
    public static final String PRINT_COUNTERS_AND_DIAGNOSTICS
            = "print-counters-and-diagnostics";
    public static final String EXIT_OVERLAY = "exit-overlay";

    public static final int MAX_NODES = 128;
}

