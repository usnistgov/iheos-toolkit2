package gov.nist.toolkit.xdstools2.shared.command.request;

import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

import java.util.List;
import java.util.Map;

public class FhirSearchRequest extends CommandContext {
    private Map<String, List<String>> codesSpec;
    private SiteSpec site;
    private String resourceTypeName;

    public FhirSearchRequest(){}
    public FhirSearchRequest(CommandContext context, SiteSpec site, String resourceTypeName, Map<String, List<String>> codesSpec){
        copyFrom(context);
        this.site=site;
        this.codesSpec=codesSpec;
        this.resourceTypeName = resourceTypeName;
    }

    public Map<String, List<String>> getCodesSpec() {
        return codesSpec;
    }

    public void setCodesSpec(Map<String, List<String>> codesSpec) {
        this.codesSpec = codesSpec;
    }

    public void setSite(SiteSpec site) {
        this.site = site;
    }

    public SiteSpec getSite() {
        return site;
    }

    public String getResourceTypeName() {
        return resourceTypeName;
    }

    public void setResourceTypeName(String resourceTypeName) {
        this.resourceTypeName = resourceTypeName;
    }
}
