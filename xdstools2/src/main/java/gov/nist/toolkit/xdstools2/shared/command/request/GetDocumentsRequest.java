package gov.nist.toolkit.xdstools2.shared.command.request;

import gov.nist.toolkit.registrymetadata.client.AnyIds;
import gov.nist.toolkit.sitemanagementui.client.SiteSpec;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

/**
 * Created by onh2 on 11/3/16.
 */
public class GetDocumentsRequest extends CommandContext{
    private AnyIds ids;
    private SiteSpec site;

    public GetDocumentsRequest(){}
    public GetDocumentsRequest(CommandContext context, SiteSpec site, AnyIds ids){
        copyFrom(context);
        this.site=site;
        this.ids=ids;
    }

    public SiteSpec getSite() {
        return site;
    }

    public void setSite(SiteSpec site) {
        this.site = site;
    }

    public void setIds(AnyIds ids) {
        this.ids = ids;
    }

    public AnyIds getIds() {
        return ids;
    }
}
