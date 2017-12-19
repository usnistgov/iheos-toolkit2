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
    private Site simProxySite = null;
    private Site simProxyBeSite = null;
    private Site regrepSite = null;

    public SrcOrchestrationResponse() {
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

    public Site getSimProxySite() {
        return simProxySite;
    }

    public void setSimProxySite(Site simProxySite) {
        this.simProxySite = simProxySite;
    }

    public Site getSimProxyBeSite() {
        return simProxyBeSite;
    }

    public void setSimProxyBeSite(Site simProxyBeSite) {
        this.simProxyBeSite = simProxyBeSite;
    }

    public Site getRegrepSite() {
        return regrepSite;
    }

    public void setRegrepSite(Site regrepSite) {
        this.regrepSite = regrepSite;
    }
}
