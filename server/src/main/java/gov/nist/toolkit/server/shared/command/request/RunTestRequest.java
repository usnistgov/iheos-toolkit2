package gov.nist.toolkit.server.shared.command.request;

import gov.nist.toolkit.server.shared.command.CommandContext;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;

import java.util.List;
import java.util.Map;

/**
 * Created by onh2 on 11/7/16.
 */
public class RunTestRequest extends CommandContext {
    private Map<String, String> params;
    private List<String> sections;
    private SiteSpec siteSpec;
    private boolean stopOnFirstFailure;
    private TestInstance testInstance;

    public RunTestRequest(){}
    public RunTestRequest(CommandContext context, SiteSpec siteSpec, TestInstance testInstance, Map<String, String> params, boolean stopOnFirstFailure){
        copyFrom(context);
        this.siteSpec=siteSpec;
        this.testInstance=testInstance;
        this.params=params;
        this.stopOnFirstFailure=stopOnFirstFailure;
    }
    public RunTestRequest(CommandContext context, SiteSpec siteSpec, TestInstance testInstance, Map<String, String> params, boolean stopOnFirstFailure, List<String> sections){
        this(context,siteSpec,testInstance,params,stopOnFirstFailure);
        this.sections=sections;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public List<String> getSections() {
        return sections;
    }

    public void setSections(List<String> sections) {
        this.sections = sections;
    }

    public SiteSpec getSiteSpec() {
        return siteSpec;
    }

    public void setSiteSpec(SiteSpec siteSpec) {
        this.siteSpec = siteSpec;
    }

    public boolean isStopOnFirstFailure() {
        return stopOnFirstFailure;
    }

    public void setStopOnFirstFailure(boolean stopOnFirstFailure) {
        this.stopOnFirstFailure = stopOnFirstFailure;
    }

    public TestInstance getTestInstance() {
        return testInstance;
    }

    public void setTestInstance(TestInstance testInstance) {
        this.testInstance = testInstance;
    }
}
