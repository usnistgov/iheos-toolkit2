package gov.nist.toolkit.desktop.shared.command.request;


import gov.nist.toolkit.desktop.client.commands.util.CommandContext;

/**
 * Created by onh2 on 10/31/16.
 */
public class GetTestdataSetListingRequest extends CommandContext {
    private String testdataSetName;

    public GetTestdataSetListingRequest(){}
    public GetTestdataSetListingRequest(CommandContext context, String testdataSetName){
        copyFrom(context);
        this.testdataSetName=testdataSetName;
    }

    public String getTestdataSetName() {
        return testdataSetName;
    }

    public void setTestdataSetName(String testdataSetName) {
        this.testdataSetName = testdataSetName;
    }
}