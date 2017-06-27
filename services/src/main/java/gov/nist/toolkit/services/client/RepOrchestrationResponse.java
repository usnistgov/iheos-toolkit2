package gov.nist.toolkit.services.client;

import gov.nist.toolkit.configDatatypes.client.Pid;
import gov.nist.toolkit.simcommon.client.SimulatorConfig;
import gov.nist.toolkit.sitemanagement.client.Site;

/**
 *
 */
public class RepOrchestrationResponse extends AbstractOrchestrationResponse {
    private Site repSite;
    private SimulatorConfig regConfig;
    private Site supportSite;
    private Pid pid;

    public RepOrchestrationResponse() {
    }

    public Site getRepSite() {
        return repSite;
    }

    public void setRepSite(Site repSite) {
        this.repSite = repSite;
    }

    public SimulatorConfig getRegConfig() {
        return regConfig;
    }

    public void setRegConfig(SimulatorConfig regConfig) {
        this.regConfig = regConfig;
    }

    public Site getSupportSite() {
        return supportSite;
    }

    public void setSupportSite(Site supportSite) {
        this.supportSite = supportSite;
    }

    public Pid getPid() {
        return pid;
    }

    public void setPid(Pid pid) {
        this.pid = pid;
    }

    @Override
    public boolean isExternalStart() {
        return false;
    }
}
