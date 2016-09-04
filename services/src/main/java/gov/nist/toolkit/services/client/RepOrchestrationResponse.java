package gov.nist.toolkit.services.client;

import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;

/**
 *
 */
public class RepOrchestrationResponse extends RawResponse {
    private SiteSpec repSite;
    private  String message = "";
    private SimulatorConfig regConfig;

    public RepOrchestrationResponse() {
    }

    public SiteSpec getRepSite() {
        return repSite;
    }

    public void setRepSite(SiteSpec repSite) {
        this.repSite = repSite;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public SimulatorConfig getRegConfig() {
        return regConfig;
    }

    public void setRegConfig(SimulatorConfig regConfig) {
        this.regConfig = regConfig;
    }
}
