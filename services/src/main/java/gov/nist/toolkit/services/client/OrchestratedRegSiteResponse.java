package gov.nist.toolkit.services.client;

import gov.nist.toolkit.configDatatypes.client.Pid;
import gov.nist.toolkit.simcommon.client.SimulatorConfig;
import gov.nist.toolkit.sitemanagement.client.Site;

/**
 *
 */
public abstract class OrchestratedRegSiteResponse extends AbstractOrchestrationResponse {
    private Pid registerPid = null;
    private SimulatorConfig config = null;
    private Site regSite = null;

    public OrchestratedRegSiteResponse() {
    }

    public Pid getRegisterPid() {
        return registerPid;
    }

    public void setRegisterPid(Pid registerPid) {
        this.registerPid = registerPid;
    }


    public SimulatorConfig getConfig() {
        return config;
    }

    public void setConfig(SimulatorConfig config) {
        this.config = config;
    }

    public Site getRegSite() {
        return regSite;
    }

    public void setRegSite(Site regSite) {
        this.regSite = regSite;
    }
}
