package gov.nist.toolkit.server.shared.command.request;

import gov.nist.toolkit.actorfactory.client.SimId;
import gov.nist.toolkit.server.shared.command.CommandContext;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;

import java.util.Map;

/**
 * Created by onh2 on 11/10/16.
 */
public class RegisterRequest extends CommandContext {
    private SimId oddsSimId;
    private Map<String, String> params;
    private SiteSpec registry;
    private TestInstance testInstance;
    private String username;

    public RegisterRequest(){}
    public RegisterRequest(CommandContext context, String username, TestInstance testInstance, SiteSpec registry, Map<String, String> params){
        copyFrom(context);
        this.username=username;
        this.testInstance=testInstance;
        this.registry=registry;
        this.params=params;
    }
    public RegisterRequest(CommandContext context, String username, TestInstance testInstance, SiteSpec registry, Map<String, String> params, SimId oddsSimId){
        this(context,username,testInstance,registry,params);
        this.oddsSimId=oddsSimId;
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

    public void setOddsSimId(SimId oddsSimId) {
        this.oddsSimId = oddsSimId;
    }

    public SimId getOddsSimId() {
        return oddsSimId;
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
