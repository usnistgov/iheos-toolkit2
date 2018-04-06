package gov.nist.toolkit.xdstools2.shared.command.request;

import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

public class PromoteRequest extends CommandContext {
    private SiteSpec site;

    public PromoteRequest() {}
    public PromoteRequest(CommandContext context, SiteSpec site) {
        copyFrom(context);
        this.site = site;
    }

    public SiteSpec getSite() {
        return site;
    }

    public void setSite(SiteSpec site) {
        this.site = site;
    }
}
