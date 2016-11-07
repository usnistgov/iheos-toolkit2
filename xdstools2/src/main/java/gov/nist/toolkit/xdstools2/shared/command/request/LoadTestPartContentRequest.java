package gov.nist.toolkit.xdstools2.shared.command.request;

import gov.nist.toolkit.session.client.logtypes.TestPartFileDTO;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

/**
 * Created by onh2 on 11/7/16.
 */
public class LoadTestPartContentRequest extends CommandContext{
    private TestPartFileDTO testPartFileDTO;

    public LoadTestPartContentRequest(){}
    public LoadTestPartContentRequest(CommandContext context,TestPartFileDTO testPartFileDTO){
        copyFrom(context);
        this.testPartFileDTO=testPartFileDTO;
    }

    public TestPartFileDTO getTestPartFileDTO() {
        return testPartFileDTO;
    }

    public void setTestPartFileDTO(TestPartFileDTO testPartFileDTO) {
        this.testPartFileDTO = testPartFileDTO;
    }
}
