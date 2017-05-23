package gov.nist.toolkit.desktop.shared.command.request;


import gov.nist.toolkit.desktop.client.commands.util.CommandContext;

/**
 * Created by onh2 on 11/7/16.
 */
public class GetTestDetailsRequest extends CommandContext {
    private String test;

    public GetTestDetailsRequest() {
    }

    public GetTestDetailsRequest(CommandContext context, String test) {
        copyFrom(context);
        this.test = test;
    }

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }
}
