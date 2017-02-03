package gov.nist.toolkit.xdstools2.shared.command.request;

import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

/**
 * Created by onh2 on 11/10/16.
 */
public class GetSectionTestPartFileRequest extends CommandContext{
    private String section;
    private TestInstance testInstance;

    public GetSectionTestPartFileRequest(){}
    public GetSectionTestPartFileRequest(CommandContext context, TestInstance testInstance, String section){
        copyFrom(context);
        this.testInstance=testInstance;
        this.section=section;
    }

    public TestInstance getTestInstance() {
        return testInstance;
    }

    public void setTestInstance(TestInstance testInstance) {
        this.testInstance = testInstance;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }
}
