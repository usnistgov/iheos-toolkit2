package gov.nist.toolkit.xdstools2.shared.command.request;

import gov.nist.toolkit.registrymetadata.client.ObjectRef;
import gov.nist.toolkit.actortransaction.shared.SiteSpec;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

import java.util.List;

/**
 * Created by onh2 on 11/4/16.
 */
public class GetRelatedRequest extends CommandContext{
    private List<String> assocs;
    private ObjectRef objectRef;
    private SiteSpec site;

    public GetRelatedRequest(){}
    public GetRelatedRequest(CommandContext context, SiteSpec site, ObjectRef objectRef, List<String> assocs){
        copyFrom(context);
        this.site=site;
        this.objectRef=objectRef;
        this.assocs=assocs;
    }

    public SiteSpec getSite() {
        return site;
    }

    public void setSite(SiteSpec site) {
        this.site = site;
    }

    public List<String> getAssocs() {
        return assocs;
    }

    public void setAssocs(List<String> assocs) {
        this.assocs = assocs;
    }

    public ObjectRef getObjectRef() {
        return objectRef;
    }

    public void setObjectRef(ObjectRef objectRef) {
        this.objectRef = objectRef;
    }
}
