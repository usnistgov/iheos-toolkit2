package gov.nist.toolkit.services.client;

import gov.nist.toolkit.configDatatypes.client.Pid;
import gov.nist.toolkit.simcommon.client.SimulatorConfig;
import gov.nist.toolkit.sitemanagement.client.Site;

/**
 *
 */
public class RecOrchestrationResponse extends AbstractOrchestrationResponse {
    private Pid registerPid = null;
    private SimulatorConfig rrConfig = null;
    private SimulatorConfig simProxyConfig = null;
    private Site supportSite = null;  // not used in UI

    public RecOrchestrationResponse() {
    }

    public Pid getRegisterPid() {
        return registerPid;
    }

    public void setRegisterPid(Pid registerPid) {
        this.registerPid = registerPid;
    }

    public SimulatorConfig getRrConfig() {
        return rrConfig;
    }

    public void setRrConfig(SimulatorConfig rrConfig) {
        this.rrConfig = rrConfig;
    }

    public Site getSupportSite() {
        return supportSite;
    }

    public void setSupportSite(Site supportSite) {
        this.supportSite = supportSite;
    }

    public SimulatorConfig getSimProxyConfig() {
        return simProxyConfig;
    }

    public void setSimProxyConfig(SimulatorConfig simProxyConfig) {
        this.simProxyConfig = simProxyConfig;
    }

    @Override
    public boolean isExternalStart() {
        return false;
    }
}
