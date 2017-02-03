package gov.nist.toolkit.xdstools2.shared.command.request;

import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

import java.util.List;

/**
 * Created by onh2 on 10/31/16.
 */
public class FindDocumentsRequest extends CommandContext{
    private boolean onDemand;
    private String pid;
    private List<String> refIds;
    private SiteSpec siteSpec;

    public FindDocumentsRequest(){}
    public FindDocumentsRequest(CommandContext context, SiteSpec site, String pid, List<String> refIds){
        copyFrom(context);
        this.siteSpec=site;
        this.pid=pid;
        this.refIds=refIds;
    }
    public FindDocumentsRequest(CommandContext context, SiteSpec site, String pid, boolean onDemand){
        copyFrom(context);
        this.siteSpec=site;
        this.pid=pid;
        this.onDemand=onDemand;
    }

    public List<String> getRefIds() {
        return refIds;
    }

    public void setRefIds(List<String> refIds) {
        this.refIds = refIds;
    }

    public SiteSpec getSiteSpec() {
        return siteSpec;
    }

    public void setSiteSpec(SiteSpec siteSpec) {
        this.siteSpec = siteSpec;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public boolean isOnDemand() {
        return onDemand;
    }

    public void setOnDemand(boolean onDemand) {
        this.onDemand = onDemand;
    }
}
