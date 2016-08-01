package gov.nist.toolkit.services.client;

import gov.nist.toolkit.actorfactory.client.SimulatorConfig;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class IdsOrchestrationResponse extends RawResponse {
    SimulatorConfig regrepConfig;

    public SimulatorConfig getRegrepConfig() {
        return regrepConfig;
    }

    public void setRegrepConfig(SimulatorConfig regrepConfig) {
        this.regrepConfig = regrepConfig;
    }

    public IdsOrchestrationResponse() {}

}
