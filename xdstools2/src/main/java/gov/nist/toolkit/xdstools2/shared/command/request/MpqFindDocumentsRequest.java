package gov.nist.toolkit.xdstools2.shared.command.request;

import gov.nist.toolkit.actortransaction.shared.SiteSpec;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

import java.util.List;
import java.util.Map;

/**
 * Created by onh2 on 11/14/16.
 */
public class MpqFindDocumentsRequest extends CommandContext{
    private Map<String, List<String>> selectedCodes;
    private String pid;
    private SiteSpec site;

    public MpqFindDocumentsRequest(){}
    public MpqFindDocumentsRequest(CommandContext context, SiteSpec site, String pid, Map<String, List<String>> selectedCodes){
        copyFrom(context);
        this.site=site;
        this.pid=pid;
        this.selectedCodes=selectedCodes;
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

    public Map<String, List<String>> getSelectedCodes() {
        return selectedCodes;
    }

    public void setSelectedCodes(Map<String, List<String>> selectedCodes) {
        this.selectedCodes = selectedCodes;
    }
}
