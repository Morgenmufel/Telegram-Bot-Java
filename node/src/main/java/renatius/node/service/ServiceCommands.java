package renatius.node.service;

public enum ServiceCommands {
    HELP("/help"),
    REGISTRATION("/registration"),
    CANCEL("/cancel"),
    START("/start"),
    TICKETS("/tickets");
    private final String cmd;

    ServiceCommands(String cmd) {
        this.cmd = cmd;
    }

    @Override
    public String toString() {
        return cmd;
    }

    public boolean equals(String cmd) {
        return this.toString().equals(cmd);
    }
}
