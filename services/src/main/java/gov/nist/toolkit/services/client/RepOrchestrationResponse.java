package gov.nist.toolkit.services.client;

import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.configDatatypes.client.Pid;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;

/**
 *
 */
public class RepOrchestrationResponse extends AbstractOrchestrationResponse {
    private SiteSpec repSite;
    private SimulatorConfig regConfig;
    private SiteSpec supportSite;
    private Pid pid;

    public RepOrchestrationResponse() {
    }

    public SiteSpec getRepSite() {
        return repSite;
    }

    public void setRepSite(SiteSpec repSite) {
        this.repSite = repSite;
    }

    public SimulatorConfig getRegConfig() {
        return regConfig;
    }

    public void setRegConfig(SimulatorConfig regConfig) {
        this.regConfig = regConfig;
    }

    public SiteSpec getSupportSite() {
        return supportSite;
    }

    public void setSupportSite(SiteSpec supportSite) {
        this.supportSite = supportSite;
    }

    public Pid getPid() {
        return pid;
    }

    public void setPid(Pid pid) {
        this.pid = pid;
    }
}
