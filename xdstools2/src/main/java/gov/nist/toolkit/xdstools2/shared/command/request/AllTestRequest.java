package gov.nist.toolkit.xdstools2.shared.command.request;

import gov.nist.toolkit.sitemanagementui.client.Site;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

/**
 * Created by onh2 on 11/10/16.
 */
public class AllTestRequest extends CommandContext{
    private Site site;

    public AllTestRequest(){}
    public AllTestRequest(CommandContext context,Site site){
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
