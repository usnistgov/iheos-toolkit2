package gov.nist.toolkit.xdstools2.shared.command;

import com.google.gwt.user.client.rpc.IsSerializable;
import gov.nist.toolkit.installation.shared.TestSession;

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

    public void setTestSessionName(String testSessionName) {
        this.testSessionName = testSessionName;
    }

    public TestSession getTestSession() {
        return new TestSession(testSessionName);
    }

    public CommandContext withTestSession(String testSession) {
        this.testSessionName = testSession;
        return this;
    }
}
