package gov.nist.toolkit.desktop.shared.command.request;

import gov.nist.toolkit.desktop.client.commands.util.CommandContext;
import gov.nist.toolkit.registrymetadata.client.AnyIds;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;

/**
 * Created by onh2 on 11/4/16.
 */
public class GetSubmissionSetsRequest extends CommandContext {
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
