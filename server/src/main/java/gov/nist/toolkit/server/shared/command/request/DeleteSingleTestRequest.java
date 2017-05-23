package gov.nist.toolkit.server.shared.command.request;

import gov.nist.toolkit.server.shared.command.CommandContext;
import gov.nist.toolkit.results.client.TestInstance;

/**
 * Created by onh2 on 11/10/16.
 */
public class DeleteSingleTestRequest extends CommandContext {
    private TestInstance testInstance;

    public DeleteSingleTestRequest(){}
    public DeleteSingleTestRequest(CommandContext context, TestInstance testInstance){
        copyFrom(context);
        this.testInstance=testInstance;
    }

    public TestInstance getTestInstance() {
        return testInstance;
    }

    public void setTestInstance(TestInstance testInstance) {
        this.testInstance = testInstance;
    }
}
