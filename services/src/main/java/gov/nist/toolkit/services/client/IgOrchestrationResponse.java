package gov.nist.toolkit.services.client;

import gov.nist.toolkit.actorfactory.client.Pid;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class IgOrchestrationResponse extends RawResponse {
    List<SimulatorConfig> simulatorConfigs = new ArrayList<>();
    Pid patientId;

    IgOrchestrationResponse() {}

    public IgOrchestrationResponse(List<SimulatorConfig> simulatorConfigs, Pid patientId) {
        this.simulatorConfigs = simulatorConfigs;
        this.patientId = patientId;
    }

    public List<SimulatorConfig> getSimulatorConfigs() {
        return simulatorConfigs;
    }

    public Pid getPatientId() {
        return patientId;
    }

    public void setPatientId(Pid patientId) {
        this.patientId = patientId;
    }

    public void setSimulatorConfigs(List<SimulatorConfig> simulatorConfigs) {
        this.simulatorConfigs = simulatorConfigs;
    }
}
