package gov.nist.toolkit.desktop.shared.command.request;


import gov.nist.toolkit.desktop.client.commands.util.CommandContext;

/**
 * Created by onh2 on 10/31/16.
 */
public class GetSiteRequest extends CommandContext {
    private String siteName;

    public GetSiteRequest(){}
    public GetSiteRequest(CommandContext context, String siteName){
        copyFrom(context);
        this.siteName=siteName;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }
}
