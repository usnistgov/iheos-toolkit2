package gov.nist.toolkit.desktop.shared.command.request;

import gov.nist.toolkit.desktop.client.commands.util.CommandContext;
import gov.nist.toolkit.registrymetadata.client.Uids;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;

/**
 * Created by onh2 on 11/4/16.
 */
public class RetrieveDocumentRequest extends CommandContext {
    private Uids uids;
    private SiteSpec site;

    public RetrieveDocumentRequest(){}
    public RetrieveDocumentRequest(CommandContext context, SiteSpec site, Uids uids){
        copyFrom(context);
        this.site=site;
        this.uids=uids;
    }

    public SiteSpec getSite() {
        return site;
    }

    public void setSite(SiteSpec site) {
        this.site = site;
    }

    public Uids getUids() {
        return uids;
    }

    public void setUids(Uids uids) {
        this.uids = uids;
    }
}
