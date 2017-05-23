package gov.nist.toolkit.server.shared.command.request;

import gov.nist.toolkit.server.shared.command.CommandContext;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;

import java.util.Map;

/**
 * Created by skb1 on 1/17/17.
 */
public class GetStsSamlAssertionMapRequest extends CommandContext {
    private Map<String, String> params;
    private SiteSpec siteSpec;
    private TestInstance testInstance;

    public GetStsSamlAssertionMapRequest(){}
    public GetStsSamlAssertionMapRequest(CommandContext context, TestInstance testInstance, SiteSpec siteSpec, Map<String, String> params){
        copyFrom(context);
        this.testInstance=testInstance;
        this.siteSpec = siteSpec;
        this.params=params;
    }
    public TestInstance getTestInstance() {
        return testInstance;
    }

    public void setTestInstance(TestInstance testInstance) {
        this.testInstance = testInstance;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public SiteSpec getSiteSpec() {
        return siteSpec;
    }

    public void setSiteSpec(SiteSpec siteSpec) {
        this.siteSpec = siteSpec;
    }

}
