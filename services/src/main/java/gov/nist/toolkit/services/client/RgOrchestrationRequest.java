package gov.nist.toolkit.services.client;

import gov.nist.toolkit.results.client.SiteSpec;

import java.io.Serializable;

/**
 *
 */
public class RgOrchestrationRequest implements Serializable {
    String userName;
    String environmentName;
    SiteSpec siteUnderTest;
    boolean useExposedRR;
    boolean useSimAsSUT;

    public RgOrchestrationRequest() {}

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEnvironmentName() {
        return environmentName;
    }

    public void setEnvironmentName(String environmentName) {
        this.environmentName = environmentName;
    }

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
}
