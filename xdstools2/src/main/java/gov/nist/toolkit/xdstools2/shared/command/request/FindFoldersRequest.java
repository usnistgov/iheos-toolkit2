package gov.nist.toolkit.xdstools2.shared.command.request;

import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

/**
 * Created by onh2 on 10/31/16.
 */
public class FindFoldersRequest extends CommandContext{
    private String pid;
    private SiteSpec site;

    public FindFoldersRequest(){}
    public FindFoldersRequest(CommandContext context, SiteSpec site, String pid){
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
