package gov.nist.toolkit.services.client;

import gov.nist.toolkit.sitemanagementui.client.SiteSpec;

/**
 *
 */
public class RepOrchestrationRequest extends AbstractOrchestrationRequest {
    private boolean useExistingSimulator = true;
    private SiteSpec sutSite;

    public RepOrchestrationRequest() {
    }

    public boolean isUseExistingSimulator() {
        return useExistingSimulator;
    }

    public void setUseExistingSimulator(boolean useExistingSimulator) {
        this.useExistingSimulator = useExistingSimulator;
    }

    public SiteSpec getSutSite() {
        return sutSite;
    }

    public void setSutSite(SiteSpec sutSite) {
        this.sutSite = sutSite;
    }
}
