package gov.nist.toolkit.xdstools2.client.command;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;

/**
 *
 */
public class CommandContext implements Serializable, IsSerializable {
    private String environmentName;
    private String testSessionName;

    public CommandContext() {
    }

    public CommandContext(String environmentName, String testSessionName) {
        this.environmentName = environmentName;
        this.testSessionName = testSessionName;
    }

    public String getEnvironmentName() {
        return environmentName;
    }

    public void setEnvironmentName(String environmentName) {
        this.environmentName = environmentName;
    }

    public String getTestSessionName() {
        return testSessionName;
    }

    public void setTestSessionName(String testSessionName) {
        this.testSessionName = testSessionName;
    }
}
