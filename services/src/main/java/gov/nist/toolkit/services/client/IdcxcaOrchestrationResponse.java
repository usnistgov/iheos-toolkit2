/**
 *
 */
package gov.nist.toolkit.services.client;

import gov.nist.toolkit.actorfactory.client.SimulatorConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Orchestration Response for Image Document Consumer in XCA-I SUT
 */
public class IdcxcaOrchestrationResponse extends AbstractOrchestrationResponse {

    private static final long serialVersionUID = 1L;

    private List<SimulatorConfig> simulatorConfigs = new ArrayList<>();
    private SimulatorConfig sutSimulatorConfig;

    /**
     * @return the {@link #simulatorConfigs} value.
     */
    public List<SimulatorConfig> getSimulatorConfigs() {
        return simulatorConfigs;
    }

    /**
     * @param simulatorConfigs the {@link #simulatorConfigs} to set
     */
    public void setSimulatorConfigs(List<SimulatorConfig> simulatorConfigs) {
        this.simulatorConfigs = simulatorConfigs;
    }

    /**
     * @return the {@link #sutSimulatorConfig} value.
     */
    public SimulatorConfig getSUTSimulatorConfig() {
        return sutSimulatorConfig;
    }

    /**
     * @param sutSimulatorConfig the {@link #sutSimulatorConfig} to set
     */
    public void setSUTSimulatorConfig(SimulatorConfig sutSimulatorConfig) {
        this.sutSimulatorConfig = sutSimulatorConfig;
    }

    /* (non-Javadoc)
     * @see gov.nist.toolkit.services.client.AbstractOrchestrationResponse#isExternalStart()
     */
    @Override
    public boolean isExternalStart() {
        return false;
    }

}
