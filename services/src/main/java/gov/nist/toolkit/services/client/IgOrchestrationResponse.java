package gov.nist.toolkit.services.client;

import gov.nist.toolkit.actorfactory.client.SimulatorConfig;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class IgOrchestrationResponse extends RawResponse {
    List<SimulatorConfig> simulatorConfigs = new ArrayList<>();

    IgOrchestrationResponse() {}

    public IgOrchestrationResponse(List<SimulatorConfig> simulatorConfigs) {
        this.simulatorConfigs = simulatorConfigs;
    }

    public List<SimulatorConfig> getSimulatorConfigs() {
        return simulatorConfigs;
    }
}
