package gov.nist.toolkit.desktop.shared.command.request;

import gov.nist.toolkit.desktop.client.commands.util.CommandContext;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;

import java.util.List;
import java.util.Map;

/**
 * Created by onh2 on 11/14/16.
 */
public class FindDocuments2Request extends CommandContext {
    private Map<String, List<String>> codesSpec;
    private String pid;
    private SiteSpec site;

    public FindDocuments2Request(){}
    public FindDocuments2Request(CommandContext context, SiteSpec site, String pid, Map<String, List<String>> codesSpec){
        copyFrom(context);
        this.site=site;
        this.pid=pid;
        this.codesSpec=codesSpec;
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

    public Map<String, List<String>> getCodesSpec() {
        return codesSpec;
    }

    public void setCodesSpec(Map<String, List<String>> codesSpec) {
        this.codesSpec = codesSpec;
    }
}
