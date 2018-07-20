package gov.nist.toolkit.xdstools2.shared.command.request;

import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.testenginelogging.client.QuickScanAttribute;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

import java.util.List;

/**
 * Created by onh2 on 10/19/16.
 */
public class GetTestsOverviewRequest extends CommandContext{
    private List<TestInstance> testInstances;
    private SiteSpec currentSiteSpec;
    private Site siteUnderTest = null;
    private QuickScanAttribute[] quickScanAttributes;

    public GetTestsOverviewRequest(){}

    public GetTestsOverviewRequest(CommandContext commandContext, List<TestInstance> testInstances) {
        copyFrom(commandContext);
        this.testInstances=testInstances;
    }
    public GetTestsOverviewRequest(CommandContext commandContext, List<TestInstance> testInstances, SiteSpec currentSiteSpec, Site siteUnderTest) {
        this(commandContext, testInstances);
        this.currentSiteSpec = currentSiteSpec;
        this.siteUnderTest = siteUnderTest;
    }
    public GetTestsOverviewRequest(CommandContext commandContext, List<TestInstance> testInstances, QuickScanAttribute[] quickScanAttributes) {
        this(commandContext, testInstances);
        this.quickScanAttributes = quickScanAttributes;
    }

    public List<TestInstance> getTestInstances() {
        return testInstances;
    }

    public void setTestInstances(List<TestInstance> testInstances) {
        this.testInstances = testInstances;
    }

    public SiteSpec getCurrentSiteSpec() {
        return currentSiteSpec;
    }

    public void setCurrentSiteSpec(SiteSpec currentSiteSpec) {
        this.currentSiteSpec = currentSiteSpec;
    }

    public Site getSiteUnderTest() {
        return siteUnderTest;
    }

    public void setSiteUnderTest(Site siteUnderTest) {
        this.siteUnderTest = siteUnderTest;
    }

    public QuickScanAttribute[] getQuickScanAttributes() {
        return quickScanAttributes;
    }

    public void setQuickScanAttributes(QuickScanAttribute[] quickScanAttributes) {
        this.quickScanAttributes = quickScanAttributes;
    }
}
