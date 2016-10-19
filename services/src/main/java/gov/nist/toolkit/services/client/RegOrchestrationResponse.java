package gov.nist.toolkit.services.client;

import gov.nist.toolkit.configDatatypes.client.Pid;

/**
 *
 */
public class RegOrchestrationResponse extends AbstractOrchestrationResponse {
    private Pid registerPid;
    private Pid sqPid;
    private Pid mpq1Pid;
    private Pid mpq2Pid;

    public RegOrchestrationResponse() {
    }

    public Pid getRegisterPid() {
        return registerPid;
    }

    public void setRegisterPid(Pid registerPid) {
        this.registerPid = registerPid;
    }

    public Pid getSqPid() {
        return sqPid;
    }

    public void setSqPid(Pid sqPid) {
        this.sqPid = sqPid;
    }

    public Pid getMpq1Pid() {
        return mpq1Pid;
    }

    public void setMpq1Pid(Pid mpq1Pid) {
        this.mpq1Pid = mpq1Pid;
    }

    public Pid getMpq2Pid() {
        return mpq2Pid;
    }

    public void setMpq2Pid(Pid mpq2Pid) {
        this.mpq2Pid = mpq2Pid;
    }

    @Override
    public boolean isExternalStart() {
        return false;
    }
}
