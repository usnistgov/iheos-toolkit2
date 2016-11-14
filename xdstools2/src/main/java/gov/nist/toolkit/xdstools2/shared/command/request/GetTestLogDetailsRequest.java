package gov.nist.toolkit.xdstools2.shared.command.request;

import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

/**
 * Created by onh2 on 11/10/16.
 */
public class GetTestLogDetailsRequest extends CommandContext{
    private TestInstance testInstance;

    public GetTestLogDetailsRequest(){}
    public GetTestLogDetailsRequest(CommandContext context,TestInstance testInstance){
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
