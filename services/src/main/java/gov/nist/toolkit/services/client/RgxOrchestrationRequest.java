package gov.nist.toolkit.services.client;

import gov.nist.toolkit.sitemanagement.client.SiteSpec;

/**
 *
 */
public class RgxOrchestrationRequest extends AbstractOrchestrationRequest {
    private SiteSpec siteUnderTest;
    private boolean useExposedRR;  // alternative is External RR (Registry/Repository)
    private boolean onDemand;  // SUT is RG with OD, useExposedRR is irrelevant
    private boolean useSimAsSUT;   // no longer used

    public RgxOrchestrationRequest() {}

    public SiteSpec getSiteUnderTest() {
        return siteUnderTest;
    }

    public void setSiteUnderTest(SiteSpec siteUnderTest) {
        this.siteUnderTest = siteUnderTest;
    }

    public boolean isUseExposedRR() {
        return useExposedRR;
    }

    public void setUseExposedRR(boolean useExposedRR) {
        this.useExposedRR = useExposedRR;
    }

    public boolean isUseSimAsSUT() {
        return useSimAsSUT;
    }

    public void setUseSimAsSUT(boolean useSimAsSUT) {
        this.useSimAsSUT = useSimAsSUT;
    }

    public boolean isOnDemand() {
        return onDemand;
    }

    public void setOnDemand(boolean onDemand) {
        this.onDemand = onDemand;
    }

}
