package gov.nist.toolkit.services.client;

import gov.nist.toolkit.actorfactory.client.Pid;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;

/**
 *
 */
public class RgOrchestrationResponse  extends RawResponse {
    Pid pid;
    SimulatorConfig regrepConfig;

    public RgOrchestrationResponse() {}

    public Pid getPid() {
        return pid;
    }

    public void setPid(Pid pid) {
        this.pid = pid;
    }

    public SimulatorConfig getRegrepConfig() {
        return regrepConfig;
    }

    public void setRegrepConfig(SimulatorConfig regrepConfig) {
        this.regrepConfig = regrepConfig;
    }
}
