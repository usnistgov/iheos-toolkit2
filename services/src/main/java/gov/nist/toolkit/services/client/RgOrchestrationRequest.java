package gov.nist.toolkit.services.client;

import com.google.gwt.user.client.rpc.IsSerializable;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;

import java.io.Serializable;

/**
 *
 */
public class RgOrchestrationRequest implements Serializable, IsSerializable {
    private String userName;
    private String environmentName;
    private SiteSpec siteUnderTest;
    private boolean useExposedRR;
    private boolean useSimAsSUT;   // no longer used
    private PifType pifType;
    private boolean useExistingSimulator = true;

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

    public PifType getPifType() {
        return pifType;
    }

    public void setPifType(PifType pifType) {
        this.pifType = pifType;
    }

    public boolean isUseExistingSimulator() {
        return useExistingSimulator;
    }

    public void setUseExistingSimulator(boolean useExistingSimulator) {
        this.useExistingSimulator = useExistingSimulator;
    }

}
