package gov.nist.toolkit.desktop.shared.command.request;

import gov.nist.toolkit.desktop.client.commands.util.CommandContext;
import gov.nist.toolkit.registrymetadata.client.ObjectRefs;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;

/**
 * Created by onh2 on 11/3/16.
 */
public class GetAssociationsRequest extends CommandContext {
    private ObjectRefs ids;
    private SiteSpec site;

    public GetAssociationsRequest(){}
    public GetAssociationsRequest(CommandContext context, SiteSpec site, ObjectRefs ids){
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

    public ObjectRefs getIds() {
        return ids;
    }

    public void setIds(ObjectRefs ids) {
        this.ids = ids;
    }
}
