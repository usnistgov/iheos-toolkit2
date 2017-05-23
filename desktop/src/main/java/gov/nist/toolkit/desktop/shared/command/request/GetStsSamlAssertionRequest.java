package gov.nist.toolkit.desktop.shared.command.request;

import gov.nist.toolkit.desktop.client.commands.util.CommandContext;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;

import java.util.Map;

/**
 *
 */
public class GetStsSamlAssertionRequest extends CommandContext {
    private Map<String, String> params;
    private SiteSpec registry;
    private TestInstance testInstance;
    private String username;

    public GetStsSamlAssertionRequest(){}
    public GetStsSamlAssertionRequest(CommandContext context, String username, TestInstance testInstance, SiteSpec registry, Map<String, String> params){
        copyFrom(context);
        this.username=username;
        this.testInstance=testInstance;
        this.registry=registry;
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

    public SiteSpec getRegistry() {
        return registry;
    }

    public void setRegistry(SiteSpec registry) {
        this.registry = registry;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
