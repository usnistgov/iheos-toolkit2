package gov.nist.toolkit.desktop.shared.command.request;

import gov.nist.toolkit.desktop.client.commands.util.CommandContext;
import gov.nist.toolkit.session.client.logtypes.TestPartFileDTO;

/**
 * Created by onh2 on 11/7/16.
 */
public class LoadTestPartContentRequest extends CommandContext {
    private TestPartFileDTO testPartFileDTO;

    public LoadTestPartContentRequest(){}
    public LoadTestPartContentRequest(CommandContext context, TestPartFileDTO testPartFileDTO){
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
