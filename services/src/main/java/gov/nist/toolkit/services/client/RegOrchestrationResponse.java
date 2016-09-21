package gov.nist.toolkit.services.client;

import gov.nist.toolkit.configDatatypes.client.Pid;

/**
 *
 */
public class RegOrchestrationResponse extends AbstractOrchestrationResponse {
    private Pid pid;
    private  String message = "";

    public RegOrchestrationResponse() {
    }

    public Pid getPid() {
        return pid;
    }

    public void setPid(Pid pid) {
        this.pid = pid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
