package gov.nist.toolkit.xdstools2.shared.command.request;

import gov.nist.toolkit.sitemanagementui.client.SiteSpec;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

/**
 * Created by onh2 on 11/14/16.
 */
public class LifecycleValidationRequest extends CommandContext{
    private String pid;
    private SiteSpec site;

    public LifecycleValidationRequest(){}
    public LifecycleValidationRequest(CommandContext context, SiteSpec site, String pid){
        copyFrom(context);
        this.site=site;
        this.pid=pid;
    }

    public SiteSpec getSite() {
        return site;
    }

    public void setSite(SiteSpec site) {
        this.site = site;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }
}
