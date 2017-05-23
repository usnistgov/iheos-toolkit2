package gov.nist.toolkit.desktop.shared.command.request;

import gov.nist.toolkit.desktop.client.commands.util.CommandContext;
import gov.nist.toolkit.sitemanagement.client.Site;

/**
 * Created by onh2 on 11/10/16.
 */
public class AllTestRequest extends CommandContext {
    private Site site;

    public AllTestRequest(){}
    public AllTestRequest(CommandContext context, Site site){
        copyFrom(context);
        this.site=site;
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }
}
