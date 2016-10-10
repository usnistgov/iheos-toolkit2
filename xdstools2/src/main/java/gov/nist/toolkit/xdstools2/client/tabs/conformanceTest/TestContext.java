package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

import com.google.gwt.user.client.rpc.AsyncCallback;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.xdstools2.client.PopupMessage;
import gov.nist.toolkit.xdstools2.client.ToolWindow;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;

/**
 *
 */
public class TestContext implements SiteManager {
    private ToolWindow toolWindow;
    private SiteSpec currentSiteSpec = new SiteSpec();
    private Site siteUnderTest = null;
    private TestContextDisplay testContextDisplay;
    static final protected String NONE = "--none--";

    public TestContext(ToolWindow toolWindow) {
        this.toolWindow = toolWindow;
    }

    public void setTestContextDisplay(TestContextDisplay testContextDisplay) {
        this.testContextDisplay = testContextDisplay;
    }

    public String verifyTestContext() {
        String msg;
        msg = verifyEnvironmentSelection();
        if (msg != null) return msg;

        msg = verifyTestSession();
        if (msg != null) return msg;

        msg = verifySite();
        if (msg != null) return msg;

        return null;  // good
    }

    private String verifyEnvironmentSelection() {
        if (toolWindow.getEnvironmentSelection() != null) return null;
        return "Environment must be selected before you proceed.";
    }

    private String verifyTestSession() {
        if (toolWindow.getCurrentTestSession() != null) return null;
        return "Test Session must be selected before you proceed.";
    }

    private String verifySite() {
        if (getSiteName() != null) return null;
        return "System under test must be selected before you proceed.";
    }

    @Override
    public String getSiteName() {
        return currentSiteSpec.getName();
    }

    @Override
    public void setSiteName(String site) {
        currentSiteSpec.setName(site);
//		getToolkitServices().getSite(site, new AsyncCallback<Site>() {
        if (site == null) return;
        if (site.equals(NONE)) return;
        ClientUtils.INSTANCE.getToolkitServices().getSite(currentSiteSpec.getName(), new AsyncCallback<Site>() {
            @Override
            public void onFailure(Throwable throwable) {
                new PopupMessage("System " + currentSiteSpec.getName() + " does not exist.");
                currentSiteSpec.setName(null);
                siteUnderTest = null;
                testContextDisplay.updateTestingContextDisplay();
            }

            @Override
            public void onSuccess(Site site) {
                siteUnderTest = site;
            }
        });
    }

    @Override
    public void update() {
        testContextDisplay.updateTestingContextDisplay();
    }


    public SiteSpec getCurrentSiteSpec() {
        return currentSiteSpec;
    }

    @Override
    public Site getSiteUnderTest() {
        return siteUnderTest;
    }

    public SiteSpec getSiteUnderTestAsSiteSpec() {
        return siteUnderTest.siteSpec();
    }

    public void setSiteUnderTest(Site siteUnderTest) {
        this.siteUnderTest = siteUnderTest;
    }

    public String getTestSession() {
        return toolWindow.getCurrentTestSession();
    }
}
