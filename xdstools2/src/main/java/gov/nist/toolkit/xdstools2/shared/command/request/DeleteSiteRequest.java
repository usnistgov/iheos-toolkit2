package gov.nist.toolkit.xdstools2.shared.command.request;

import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

/**
 * Created by onh2 on 10/31/16.
 */
public class DeleteSiteRequest extends CommandContext{
    private String siteName;

    public DeleteSiteRequest() {}
    public DeleteSiteRequest(CommandContext commandContext, String name) {
        copyFrom(commandContext);
        this.siteName=name;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }
}
