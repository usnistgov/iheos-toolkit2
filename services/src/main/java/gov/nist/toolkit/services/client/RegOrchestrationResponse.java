package gov.nist.toolkit.services.client;

import gov.nist.toolkit.configDatatypes.client.Pid;

/**
 *
 */
public class RegOrchestrationResponse extends AbstractOrchestrationResponse {
    private Pid pid;

    public RegOrchestrationResponse() {
    }

    public Pid getPid() {
        return pid;
    }

    public void setPid(Pid pid) {
        this.pid = pid;
    }

}
