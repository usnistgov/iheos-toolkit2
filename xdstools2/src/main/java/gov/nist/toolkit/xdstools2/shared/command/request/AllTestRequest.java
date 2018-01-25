package gov.nist.toolkit.xdstools2.shared.command.request;

import gov.nist.toolkit.sitemanagement.client.*;
import gov.nist.toolkit.xdstools2.shared.command.*;

/**
 * Created by onh2 on 11/10/16.
 */
public class AllTestRequest extends CommandContext {
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
