package gov.nist.toolkit.server.shared.command.request;

import gov.nist.toolkit.server.shared.command.CommandContext;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;

/**
 * Created by onh2 on 10/31/16.
 */
public class FoldersRequest extends CommandContext {
    private String pid;
    private SiteSpec site;

    public FoldersRequest(){}
    public FoldersRequest(CommandContext context, SiteSpec site, String pid){
        copyFrom(context);
        this.site=site;
        this.pid=pid;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public SiteSpec getSite() {
        return site;
    }

    public void setSite(SiteSpec site) {
        this.site = site;
    }
}
