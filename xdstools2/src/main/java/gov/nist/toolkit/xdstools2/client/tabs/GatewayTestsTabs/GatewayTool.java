package gov.nist.toolkit.xdstools2.client.tabs.GatewayTestsTabs;

import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.xdstools2.client.framework.TabContainer;

/**
 *
 */
public interface GatewayTool {
//    ToolkitServiceAsync getToolkitService();
    TabContainer getToolContainer();
    String getCurrentTestSession();
    SiteSpec getSiteSelection();
    boolean verifySiteProvided();
    String getSelectedTest();
    void setSelectedTest(String test);
}
