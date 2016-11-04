package gov.nist.toolkit.xdstools2.shared.command.request;

import gov.nist.toolkit.registrymetadata.client.AnyIds;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

/**
 * Created by onh2 on 11/4/16.
 */
public class GetSubmissionSetsRequest extends CommandContext{
    private AnyIds ids;
    private SiteSpec site;

    public GetSubmissionSetsRequest(){}
    public GetSubmissionSetsRequest(CommandContext context, SiteSpec site, AnyIds ids){
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

    public AnyIds getIds() {
        return ids;
    }

    public void setIds(AnyIds ids) {
        this.ids = ids;
    }
}
