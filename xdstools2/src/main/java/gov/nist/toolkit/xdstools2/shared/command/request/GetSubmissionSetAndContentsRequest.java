package gov.nist.toolkit.xdstools2.shared.command.request;

import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

import java.util.List;
import java.util.Map;

/**
 * Created by onh2 on 10/31/16.
 */
public class GetSubmissionSetAndContentsRequest extends CommandContext{
    private SiteSpec siteSpec;
    private String ssid;
    private Map<String, List<String>> codeSpec;

    public GetSubmissionSetAndContentsRequest() {}
    public GetSubmissionSetAndContentsRequest(CommandContext commandContext, SiteSpec siteSpec, String ssid, Map<String, List<String>> codeSpec) {
        copyFrom(commandContext);
        this.siteSpec=siteSpec;
        this.ssid=ssid;
        this.codeSpec=codeSpec;
    }

    public Map<String, List<String>> getCodeSpec() {
        return codeSpec;
    }

    public void setCodeSpec(Map<String, List<String>> codeSpec) {
        this.codeSpec = codeSpec;
    }

    public SiteSpec getSiteSpec() {
        return siteSpec;
    }

    public void setSiteSpec(SiteSpec siteSpec) {
        this.siteSpec = siteSpec;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }
}
