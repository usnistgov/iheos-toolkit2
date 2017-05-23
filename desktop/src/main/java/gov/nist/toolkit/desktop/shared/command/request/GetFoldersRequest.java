package gov.nist.toolkit.desktop.shared.command.request;

import gov.nist.toolkit.desktop.client.commands.util.CommandContext;
import gov.nist.toolkit.registrymetadata.client.AnyIds;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;

/**
 * Created by onh2 on 11/3/16.
 */
public class GetFoldersRequest extends CommandContext {
    private AnyIds anyIds;
    private SiteSpec site;

    public GetFoldersRequest(){}
    public GetFoldersRequest(CommandContext context, SiteSpec site, AnyIds anyIds){
        copyFrom(context);
        this.site=site;
        this.anyIds=anyIds;
    }

    public SiteSpec getSite() {
        return site;
    }

    public void setSite(SiteSpec site) {
        this.site = site;
    }

    public AnyIds getAnyIds() {
        return anyIds;
    }

    public void setAnyIds(AnyIds anyIds) {
        this.anyIds = anyIds;
    }
}
