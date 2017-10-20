package gov.nist.toolkit.services.client;

import gov.nist.toolkit.configDatatypes.client.Pid;
import gov.nist.toolkit.simcommon.client.SimulatorConfig;
import gov.nist.toolkit.sitemanagement.client.Site;

/**
 *
 */
public class SrcOrchestrationResponse extends AbstractOrchestrationResponse {
    private Pid registerPid = null;
    private SimulatorConfig config = null;
    private Site supportSite = null;  // not used in UI

    public SrcOrchestrationResponse() {
    }

    public Pid getRegisterPid() {
        return registerPid;
    }

    public void setRegisterPid(Pid registerPid) {
        this.registerPid = registerPid;
    }


    public Site getSupportSite() {
        return supportSite;
    }

    public void setSupportSite(Site supportSite) {
        this.supportSite = supportSite;
    }

    public SimulatorConfig getConfig() {
        return config;
    }

    public void setConfig(SimulatorConfig config) {
        this.config = config;
    }

    @Override
    public boolean isExternalStart() {
        return false;
    }
}
