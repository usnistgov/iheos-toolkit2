package gov.nist.toolkit.server.shared.command;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;

/**
 *
 */
public class CommandContext implements Serializable, IsSerializable {
    // The current values
    private String environmentName;
    private String testSessionName;

    public CommandContext() {
    }

    public CommandContext(String environmentName, String testSessionName) {
        this.environmentName = environmentName;
        this.testSessionName = testSessionName;
    }

    public void copyFrom(CommandContext commandContext) {
        environmentName = commandContext.getEnvironmentName();
        testSessionName = commandContext.getTestSessionName();
    }

    public String getEnvironmentName() {
        return environmentName;
    }

    public CommandContext setEnvironmentName(String environmentName) {
        this.environmentName = environmentName;
        return this;
    }

    public String getTestSessionName() {
        return testSessionName;
    }

    public CommandContext setTestSessionName(String testSessionName) {
        this.testSessionName = testSessionName;
        return this;
    }

}
