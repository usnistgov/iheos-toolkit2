package gov.nist.toolkit.services.client;

import gov.nist.toolkit.sitemanagement.client.SiteSpec;

public class EdgeSrv5OrchestrationRequest extends AbstractOrchestrationRequest {
        private static final long serialVersionUID = 1L;
        private boolean useExistingSimulator = true;

        private SiteSpec siteUnderTest;

    public boolean isUseExistingSimulator() {
        return useExistingSimulator;
    }

    public void setUseExistingSimulator(boolean useExistingSimulator) {
        this.useExistingSimulator = useExistingSimulator;
    }

    public SiteSpec getSiteUnderTest() {
        return siteUnderTest;
    }

    public void setSiteUnderTest(SiteSpec siteUnderTest) {
        this.siteUnderTest = siteUnderTest;
    }
}
