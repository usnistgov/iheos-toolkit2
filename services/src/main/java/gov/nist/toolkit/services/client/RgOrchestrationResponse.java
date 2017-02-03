package gov.nist.toolkit.services.client;

import gov.nist.toolkit.configDatatypes.client.Pid;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;

/**
 *
 */
public class RgOrchestrationResponse  extends AbstractOrchestrationResponse {
//    private Pid oneDocPid;
//    private Pid twoDocPid;
    private Pid simplePid;
//    private Pid t12306Pid;
    private SiteSpec siteUnderTest;
    private SiteSpec regrepSite;
    private SimulatorConfig regrepConfig;
    private boolean sameSite;
    private boolean useExposedRR;  // alternative is External RR (Registry/Repository)

    public RgOrchestrationResponse() {}

//    public Pid getOneDocPid() {
//        return oneDocPid;
//    }
//
//    public void setOneDocPid(Pid oneDocPid) {
//        this.oneDocPid = oneDocPid;
//    }
//
//    public Pid getTwoDocPid() {
//        return twoDocPid;
//    }
//
//    public void setTwoDocPid(Pid twoDocPid) {
//        this.twoDocPid = twoDocPid;
//    }

    public SiteSpec getSiteUnderTest() {
        return siteUnderTest;
    }

    public void setSiteUnderTest(SiteSpec siteUnderTest) {
        this.siteUnderTest = siteUnderTest;
    }

    public SiteSpec getRegrepSite() {
        return regrepSite;
    }

    public void setRegrepSite(SiteSpec regrepSite) {
        this.regrepSite = regrepSite;
    }

    public boolean isSameSite() {
        return sameSite;
    }

    public void setSameSite(boolean sameSite) {
        this.sameSite = sameSite;
    }

    public SimulatorConfig getRegrepConfig() {
        return regrepConfig;
    }

    public void setRegrepConfig(SimulatorConfig regrepConfig) {
        this.regrepConfig = regrepConfig;
    }

    public boolean isUseExposedRR() {
        return useExposedRR;
    }

    public void setUseExposedRR(boolean useExposedRR) {
        this.useExposedRR = useExposedRR;
    }

    public Pid getSimplePid() {
        return simplePid;
    }

    public void setSimplePid(Pid simplePid) {
        this.simplePid = simplePid;
    }

    @Override
    public boolean isExternalStart() {
        return false;
    }

}
