package gov.nist.toolkit.services.client;

import gov.nist.toolkit.configDatatypes.client.Pid;
import gov.nist.toolkit.simcommon.client.SimulatorConfig;
import gov.nist.toolkit.sitemanagement.client.Site;

import java.util.Collection;

/**
 *
 */
public class RecOrchestrationResponse extends AbstractOrchestrationResponse {
    private Pid registerPid = null;
    private SimulatorConfig rrConfig = null;
    private Site rrSite = null;
    private Site supportSite = null;  // not used in UI
    private FhirSupportOrchestrationResponse supportResponse;


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

    public Site getRRSite() {
        return rrSite;
    }

    public void setRRSite(Site rrSite) {
        this.rrSite = rrSite;
    }

    @Override
    public boolean isExternalStart() {
        return false;
    }

    public FhirSupportOrchestrationResponse getSupportResponse() {
        return supportResponse;
    }

    public void setSupportResponse(FhirSupportOrchestrationResponse supportResponse) {
        this.supportResponse = supportResponse;
        Collection<MessageItem> msgs = supportResponse.getMessages();
        if (msgs != null && !msgs.isEmpty()) {
            for (MessageItem item : msgs) {
                this.addMessage(null, item.isSuccess(), item.getMessage());
            }
        }
    }
}
