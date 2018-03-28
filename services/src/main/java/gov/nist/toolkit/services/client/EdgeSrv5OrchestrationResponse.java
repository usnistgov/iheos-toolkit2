package gov.nist.toolkit.services.client;

import gov.nist.toolkit.simcommon.client.SimulatorConfig;

import java.util.ArrayList;
import java.util.List;

public class EdgeSrv5OrchestrationResponse  extends AbstractOrchestrationResponse {
    private static final long serialVersionUID = 1L;

    List<SimulatorConfig> simulatorConfigs = new ArrayList<>();
    SimulatorConfig rigSimulatorConfig;

    public EdgeSrv5OrchestrationResponse() {}

    /**
     * @return the {@link #simulatorConfigs} value.
     */
    public List <SimulatorConfig> getSimulatorConfigs() {
        return simulatorConfigs;
    }

    /**
     * @param simulatorConfigs the {@link #simulatorConfigs} to set
     */
    public void setSimulatorConfigs(List <SimulatorConfig> simulatorConfigs) {
        this.simulatorConfigs = simulatorConfigs;
    }

    /**
     * @return the {@link #rigSimulatorConfig} value.
     */
    public SimulatorConfig getRigSimulatorConfig() {
        return rigSimulatorConfig;
    }

    /**
     * @param rigSimulatorConfig the {@link #rigSimulatorConfig} to set
     */
    public void setRigSimulatorConfig(SimulatorConfig rigSimulatorConfig) {
        this.rigSimulatorConfig = rigSimulatorConfig;
    }

    /* (non-Javadoc)
     * @see gov.nist.toolkit.services.client.AbstractOrchestrationResponse#isExternalStart()
     */
    @Override
    public boolean isExternalStart() {
        // TODO Auto-generated method stub
        return false;
    };
}
