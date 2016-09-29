package gov.nist.toolkit.services.client;

import gov.nist.toolkit.configDatatypes.client.Pid;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;

/**
 *
 */
public class RgOrchestrationResponse  extends AbstractOrchestrationResponse {
    private Pid oneDocPid;
    private Pid twoDocPid;
    private SiteSpec siteUnderTest;
    private SiteSpec regrepSite;
    private SimulatorConfig regrepConfig;
    private boolean sameSite;

    public RgOrchestrationResponse() {}

    public Pid getOneDocPid() {
        return oneDocPid;
    }

    public void setOneDocPid(Pid oneDocPid) {
        this.oneDocPid = oneDocPid;
    }

    public Pid getTwoDocPid() {
        return twoDocPid;
    }

    public void setTwoDocPid(Pid twoDocPid) {
        this.twoDocPid = twoDocPid;
    }

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

}
