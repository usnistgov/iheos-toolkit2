package gov.nist.toolkit.services.client;

import gov.nist.toolkit.actorfactory.client.Pid;
import gov.nist.toolkit.results.client.SiteSpec;

/**
 *
 */
public class RgOrchestrationResponse  extends RawResponse {
    Pid pid;
    SiteSpec siteUnderTest;
    SiteSpec regrepSite;
    boolean sameSite;

    public RgOrchestrationResponse() {}

    public Pid getPid() {
        return pid;
    }

    public void setPid(Pid pid) {
        this.pid = pid;
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
}
