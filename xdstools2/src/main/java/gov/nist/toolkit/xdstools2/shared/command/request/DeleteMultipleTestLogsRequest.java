package gov.nist.toolkit.xdstools2.shared.command.request;

import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

import java.util.List;

public class DeleteMultipleTestLogsRequest extends CommandContext {
    List<TestInstance> testInstances;

    public DeleteMultipleTestLogsRequest() {
    }

    public DeleteMultipleTestLogsRequest(CommandContext commandContext, List<TestInstance> testInstances) {
        copyFrom(commandContext);
        this.testInstances = testInstances;
    }

    public List<TestInstance> getTestInstances() {
        return testInstances;
    }

    public void setTestInstances(List<TestInstance> testInstances) {
        this.testInstances = testInstances;
    }
}
