package gov.nist.toolkit.xdstools2.shared.command.request;

import gov.nist.toolkit.registrymetadata.client.ObjectRefs;
import gov.nist.toolkit.sitemanagementui.client.SiteSpec;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

/**
 * Created by onh2 on 11/4/16.
 */
public class GetObjectsRequest extends CommandContext{
    private ObjectRefs ids;
    private SiteSpec site;

    public GetObjectsRequest(){}
    public GetObjectsRequest(CommandContext context, SiteSpec site, ObjectRefs ids){
        copyFrom(context);
        this.site=site;
        this.ids=ids;
    }

    public ObjectRefs getIds() {
        return ids;
    }

    public void setIds(ObjectRefs ids) {
        this.ids = ids;
    }

    public SiteSpec getSite() {
        return site;
    }

    public void setSite(SiteSpec site) {
        this.site = site;
    }
}
