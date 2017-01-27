package gov.nist.toolkit.xdstools2.shared.command.request;

import gov.nist.toolkit.actortransaction.shared.SiteSpec;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

import java.util.List;
import java.util.Map;

/**
 * Created by onh2 on 11/14/16.
 */
public class GetAllRequest extends CommandContext{
    private Map<String, List<String>> codesSpec;
    private String pid;
    private SiteSpec site;

    public GetAllRequest(){}
    public GetAllRequest(CommandContext context, SiteSpec site, String pid, Map<String, List<String>> codesSpec){
        copyFrom(context);
        this.site=site;
        this.pid=pid;
        this.codesSpec=codesSpec;
    }

    public Map<String, List<String>> getCodesSpec() {
        return codesSpec;
    }

    public void setCodesSpec(Map<String, List<String>> codesSpec) {
        this.codesSpec = codesSpec;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public void setSite(SiteSpec site) {
        this.site = site;
    }

    public SiteSpec getSite() {
        return site;
    }
}
