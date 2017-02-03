package gov.nist.toolkit.services.client;

import gov.nist.toolkit.configDatatypes.client.Pid;

/**
 *
 */
public class RecOrchestrationResponse extends AbstractOrchestrationResponse {
    private Pid registerPid;

    public RecOrchestrationResponse() {
    }

    public Pid getRegisterPid() {
        return registerPid;
    }

    public void setRegisterPid(Pid registerPid) {
        this.registerPid = registerPid;
    }

    @Override
    public boolean isExternalStart() {
        return false;
    }
}
