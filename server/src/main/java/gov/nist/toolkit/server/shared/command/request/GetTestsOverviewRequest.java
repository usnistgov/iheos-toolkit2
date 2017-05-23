package gov.nist.toolkit.server.shared.command.request;

import gov.nist.toolkit.server.shared.command.CommandContext;
import gov.nist.toolkit.results.client.TestInstance;

import java.util.List;

/**
 * Created by onh2 on 10/19/16.
 */
public class GetTestsOverviewRequest extends CommandContext {
    private List<TestInstance> testInstances;

    public GetTestsOverviewRequest(){}
    public GetTestsOverviewRequest(CommandContext commandContext, List<TestInstance> testInstances) {
        copyFrom(commandContext);
        this.testInstances=testInstances;
    }

    public List<TestInstance> getTestInstances() {
        return testInstances;
    }

    public void setTestInstances(List<TestInstance> testInstances) {
        this.testInstances = testInstances;
    }
}
