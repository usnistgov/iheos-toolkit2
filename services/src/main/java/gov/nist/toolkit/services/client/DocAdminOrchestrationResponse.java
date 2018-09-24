package gov.nist.toolkit.services.client;

import gov.nist.toolkit.configDatatypes.client.Pid;
import gov.nist.toolkit.simcommon.client.SimulatorConfig;
import gov.nist.toolkit.sitemanagement.client.Site;

/**
 *
 */
public class DocAdminOrchestrationResponse extends AbstractOrchestrationResponse {
    private Pid registerPid = null;
    private SimulatorConfig config = null;
    private Site regSite = null;

    public DocAdminOrchestrationResponse() {
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

    @Override
    public boolean isExternalStart() {
        return false;
    }

    public Site getRegSite() {
        return regSite;
    }

    public void setRegSite(Site regSite) {
        this.regSite = regSite;
    }
}
